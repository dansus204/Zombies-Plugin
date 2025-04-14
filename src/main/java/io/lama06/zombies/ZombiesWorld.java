package io.lama06.zombies;

import io.lama06.zombies.data.*;
import io.lama06.zombies.event.GameEndEvent;
import io.lama06.zombies.event.GameStartEvent;
import io.lama06.zombies.event.zombie.ZombieSpawnEvent;
import io.lama06.zombies.perk.GlobalPerk;
import io.lama06.zombies.util.GraphDoorLink;
import io.lama06.zombies.zombie.Zombie;
import io.lama06.zombies.zombie.ZombieType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class ZombiesWorld extends Storage implements ForwardingAudience {
    public static final AttributeId<Boolean> GAME_RUNNING = new AttributeId<>("game_running", PersistentDataType.BOOLEAN);

    public static final AttributeId<Integer> START_TIMER = new AttributeId<>("start_timer", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> GAME_ID = new AttributeId<>("game_id", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> ROUND = new AttributeId<>("round", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> REMAINING_ZOMBIES = new AttributeId<>("remaining_zombies", PersistentDataType.INTEGER);

    public static final AttributeId<Integer> NEXT_ZOMBIE_TIME = new AttributeId<>("next_zombie", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> NEXT_WAVE_TIME = new AttributeId<>("next_wave", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> WAVE = new AttributeId<>("wave", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> ANGER_TIMER = new AttributeId<>("anger_timer", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> CHECK_TIMER = new AttributeId<>("check_timer", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> INDEX_UPDATE_TIMER = new AttributeId<>("index_update_timer", PersistentDataType.INTEGER);
    public static final AttributeId<Integer> HP_BAR_TIMER = new AttributeId<>("hp_bar_timer", PersistentDataType.INTEGER);


    public static final AttributeId<Boolean> BOSS_SPAWNED = new AttributeId<>("boss_spawned", PersistentDataType.BOOLEAN);
    public static final AttributeId<Boolean> POWER_SWITCH = new AttributeId<>("power_switch", PersistentDataType.BOOLEAN);
    public static final AttributeId<List<String>> REACHABLE_AREAS = new AttributeId<>("reachable_areas", PersistentDataType.LIST.strings());
    public static final AttributeId<List<Integer>> OPEN_DOORS = new AttributeId<>("open_doors", PersistentDataType.LIST.integers());
    public static final AttributeId<Integer> DRAGONS_WRATH_USED = new AttributeId<>("dragons_wrath_used", PersistentDataType.INTEGER);

    public static final ComponentId PERKS_COMPONENT = new ComponentId("perks");

    public static final ComponentId GRAPH = new ComponentId("graph");

    private final World world;

    public ZombiesWorld(final World world) {
        this.world = world;
    }

    public boolean isGameRunning() {
        return Objects.requireNonNullElse(get(GAME_RUNNING), false);
    }

    public boolean isZombiesWorld() {
        return ZombiesPlugin.INSTANCE.isZombiesWorld(this);
    }

    public WorldConfig getConfig() {
        return ZombiesPlugin.INSTANCE.getWorldConfig(this);
    }

    public void startGame() {
        set(GAME_RUNNING, true);
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
    }

    public void endGame() {
        set(GAME_RUNNING, false);
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
    }

    public List<Zombie> getZombies() {
        return world.getEntities().stream().map(Zombie::new).filter(Zombie::isZombie).toList();
    }

    public Zombie spawnZombie(final Location location, final ZombieType type) {
        final Entity entity = world.spawnEntity(location, type.data.entity, false);
        if (type.data.initializer != null) {
            type.data.initializer.accept(entity);
        }
        if (entity instanceof final LivingEntity living) {
            living.setRemoveWhenFarAway(false);
        }
        final Zombie zombie = new Zombie(entity);
        zombie.set(Zombie.IS_ZOMBIE, true);
        zombie.set(Zombie.TYPE, type);
        zombie.set(Zombie.IN_WINDOW, false);




        //zombie.set(Zombie.CLOSEST_POINT, getConfig().graph.findClosestPointIndex(location));
        Bukkit.getPluginManager().callEvent(new ZombieSpawnEvent(zombie, type.data));

        if (type.data.isBoss) {
            final ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
            stand.setNoPhysics(true);
            stand.setGravity(false);
            stand.setInvisible(true);
            stand.setInvulnerable(true);
            stand.setCustomNameVisible(true);

            entity.setCustomNameVisible(true);
            entity.customName(net.kyori.adventure.text.Component.text(type.name()));

            Bukkit.getScheduler().runTaskTimer(
                    ZombiesPlugin.INSTANCE,
                    () -> updateHealthBar(zombie, stand),
                    1, 1

            );
        }
        return zombie;
    }

    public void updateHealthBar(final Zombie zombie, final ArmorStand stand) {
        final int hpParts = (int) (((LivingEntity) zombie.getEntity()).getHealth() / zombie.getData().health * 20);
        final String string = "|".repeat(hpParts) + ".".repeat(20 - hpParts);
        stand.customName(net.kyori.adventure.text.Component.text(string).color(NamedTextColor.RED));
        stand.teleport(zombie.getEntity().getLocation());
        if (hpParts == 0) {
            stand.remove();
        }
    }

    public List<ZombiesPlayer> getAlivePlayers() {
        return world.getPlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.ADVENTURE)
                .map(ZombiesPlayer::new)
                .toList();
    }

    public List<ZombiesPlayer> getPlayers() {
        return world.getPlayers().stream().map(ZombiesPlayer::new).toList();
    }

    public boolean isPerkEnabled(final GlobalPerk perk) {
        final Component perksComponent = getComponent(PERKS_COMPONENT);
        if (perksComponent == null) {
            return false;
        }
        return perksComponent.get(perk.getRemainingTimeAttribute()) != 0;
    }

    public List<Window> getAvailableWindows() {
        final WorldConfig config = getConfig();
        if (config == null) {
            return List.of();
        }
        final List<String> areas = get(REACHABLE_AREAS);
        return config.windows.stream().filter(window -> areas.contains(window.area)).toList();
    }

    public Location getNextZombieSpawnPoint() {
        final RandomGenerator rnd = ThreadLocalRandom.current();
        final List<ZombiesPlayer> players = getPlayers();
        if (players.isEmpty()) {
            return null;
        }
        final ZombiesPlayer player = players.get(rnd.nextInt(players.size()));
        final Window window = getNearestWindowToPlayer(player);
        if (window == null) {
            return null;
        }
        return window.spawnLocation.toBukkit(getBukkit());
    }

    public void updateGraph(final String area, final List<GraphDoorLink> links) {
        final WorldConfig config = getConfig();
        if (config == null) {
            return;
        }
        config.graphPoints.forEach(graphPoint -> {
            if (area.equals(graphPoint.area)) {
                graphPoint.setLocation(world);
                config.graph.addPoint(graphPoint.copy());
            }
        });
        if (links != null) {
            links.forEach(link -> config.graph.addLink(link.first_id, link.second_id));
        }
        config.graph.calcFinalDistance();
    }

    private Window getNearestWindowToPlayer(final ZombiesPlayer player) {
        return getAvailableWindows().stream().min(Comparator.comparingDouble(window -> {
            final Vector windowVector = window.spawnLocation.toBukkit(player.getBukkit().getWorld()).toVector();
            final Vector playerVector = player.getBukkit().getLocation().toVector();
            final double xDiff = windowVector.getX() - playerVector.getX();
            final double zDiff = windowVector.getZ() - playerVector.getZ();
            final double yDiff = (windowVector.getY() - playerVector.getY()) * 6; // avoid spawning zombies on different floor
            return Math.sqrt(xDiff*xDiff + yDiff*yDiff + zDiff*zDiff);
        })).orElse(null);
    }


    @Override
    protected StorageSession startSession() {
        return world::getPersistentDataContainer;
    }

    @Override
    public Iterable<? extends Audience> audiences() {
        return Set.of(world);
    }

    public World getBukkit() {
        return world;
    }
}
