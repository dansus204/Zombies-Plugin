package io.lama06.zombies.system;

import io.lama06.zombies.*;
import io.lama06.zombies.event.GameStartEvent;
import io.lama06.zombies.perk.GlobalPerk;
import io.lama06.zombies.perk.PerkMachine;
import io.lama06.zombies.util.Graph;
import io.lama06.zombies.weapon.WeaponType;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.lama06.zombies.util.HologramUtil.placeHologram;

public final class PrepareWorldAtGameStartSystem implements Listener {



    @EventHandler
    private void onGameStart(final GameStartEvent event) {
        final ZombiesWorld world = event.getWorld();
        final WorldConfig config = world.getConfig();
        final SpawnRate firstRoundSpawnRate = SpawnRate.SPAWN_RATES.getFirst();
        final Integer lastGameId = world.get(ZombiesWorld.GAME_ID);
        final int gameId = lastGameId == null ? 1 : lastGameId + 1;

        world.getBukkit().setGameRule(GameRule.DO_FIRE_TICK, false);
        world.getBukkit().setGameRule(GameRule.MOB_GRIEFING, false);
        world.getBukkit().setGameRule(GameRule.DISABLE_RAIDS, true);

        world.set(ZombiesWorld.GAME_ID, gameId);
        world.set(ZombiesWorld.ROUND, 1);
        world.set(ZombiesWorld.WAVE, 0);
        world.set(ZombiesWorld.OPEN_DOORS, List.of());
        world.set(ZombiesWorld.REACHABLE_AREAS, List.of(config.startArea));
        world.set(ZombiesWorld.POWER_SWITCH, false);
        world.set(ZombiesWorld.NEXT_WAVE_TIME, firstRoundSpawnRate.waves().getFirst().waveDelay());
        world.set(ZombiesWorld.ANGER_TIMER, 0);
        world.set(ZombiesWorld.CHECK_TIMER, 0);
        world.set(ZombiesWorld.HP_BAR_TIMER, 0);
        world.set(ZombiesWorld.INDEX_UPDATE_TIMER, 0);
        world.set(ZombiesWorld.REMAINING_ZOMBIES, firstRoundSpawnRate.getNumberOfZombies());
        world.set(ZombiesWorld.BOSS_SPAWNED, false);
        world.set(ZombiesWorld.DRAGONS_WRATH_USED, 0);

        config.graph = new Graph();
        world.updateGraph(world.getConfig().startArea, null);

        final io.lama06.zombies.data.Component perksComponent = world.addComponent(ZombiesWorld.PERKS_COMPONENT);
        for (final GlobalPerk perk : GlobalPerk.values()) {
            perksComponent.set(perk.getRemainingTimeAttribute(), 0);
        }

        for (final Window window : config.windows) {
            window.close(event.getWorld());
        }
        for (final Door door : config.doors) {
            door.setOpen(world, false);
        }
        if (config.powerSwitch != null) {
            config.powerSwitch.setActive(world, false);
        }


        for (final ZombiesPlayer player : world.getPlayers()) {
            player.set(ZombiesPlayer.GAME_ID, gameId);
            player.set(ZombiesPlayer.KILLS, 0);
            player.set(ZombiesPlayer.GOLD, 10000);
            player.set(ZombiesPlayer.CLOSEST_POINT, 6);
            final Player bukkit = player.getBukkit();
            bukkit.getInventory().clear();
            bukkit.teleport(world.getBukkit().getSpawnLocation());
            bukkit.setFoodLevel(20);
            bukkit.setHealth(20);
            bukkit.setGameMode(GameMode.ADVENTURE);
            bukkit.setLevel(0);
            bukkit.setExp(0);
            bukkit.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 3));
            player.giveWeapon(0, WeaponType.KNIFE);
            player.giveWeapon(1, WeaponType.PISTOL);
        }

        world.setNewLuckyChest(null);
        world.updateDoorHolograms();


        for (final ArmorShop armorShop : config.armorShops) {
            if (armorShop.position == null || armorShop.quality == null || armorShop.part == null) {
                continue;
            }
            placeHologram(world.getBukkit(), armorShop.position.toCenter(), List.of(
                            armorShop.quality.getDisplayName().append(Component.text(" Armor")),
                            armorShop.part.getDisplayName(),
                            Component.text(armorShop.price + " Gold").color(NamedTextColor.GOLD)
                          )
            );


            final ArmorStand stand = placeArmorStand(this::getShopSignPosition, world.getBukkit(), armorShop.position);
            if (stand != null) {
                for (final EquipmentSlot equipmentSlot : armorShop.part.getEquipmentSlots()) {
                    final ItemStack item = new ItemStack(armorShop.quality.materials.get(equipmentSlot));
                    stand.setItem(equipmentSlot, item);
                }
            }

            final ZombiesEntity entity = new ZombiesEntity(stand);
            entity.set(ZombiesEntity.IS_NOT_VANILLA, true);
            entity.set(ZombiesEntity.SHOP_ID, armorShop.id);
        }


        for (final WeaponShop weaponShop : config.weaponShops) {
            if (weaponShop.position == null || weaponShop.weaponType == null) {
                continue;
            }
            placeHologram(world.getBukkit(), weaponShop.position.toCenter(), List.of(
                                  weaponShop.weaponType.getDisplayName(),
                                        Component.text(weaponShop.purchasePrice + " Gold").color(NamedTextColor.GOLD),
                                        Component.text("Right Click to purchase")
                          )

            );
            placeItem(this::getShopSignPosition, world.getBukkit(), weaponShop.position, weaponShop.weaponType.data.material);

            final ArmorStand stand = placeArmorStand(this::getShopSignPosition, world.getBukkit(), weaponShop.position);
            final ZombiesEntity entity = new ZombiesEntity(stand);
            entity.set(ZombiesEntity.IS_NOT_VANILLA, true);
            entity.set(ZombiesEntity.SHOP_ID, weaponShop.id);
        }

        for (final PerkMachine perkMachine : config.perkMachines) {
            if (perkMachine.position == null || perkMachine.perk == null) {
                continue;
            }
            placeHologram(world.getBukkit(), perkMachine.position.toCenter(), List.of(
                            perkMachine.perk.getDisplayName(),
                            Component.text(" " + perkMachine.gold + " Gold").color(NamedTextColor.GOLD)
                          )
            );

        }

        placeHologram(world.getBukkit(), world.getConfig().ultimateMachine.position.toCenter(), List.of(
                              Component.text("Ultimate Machine ").append(Component.text(world.getConfig().ultimateMachine.gold + " Gold").color(NamedTextColor.GOLD)),
                              Component.text("Right Click to upgrade your weapon")
                      )
        );

        placeHologram(world.getBukkit(), world.getConfig().teamMachine.toCenter(), List.of(
                              Component.text("Team Machine "),
                              Component.text("Right Click")
                      )
        );

        placeHologram(world.getBukkit(), world.getConfig().powerSwitch.position.toCenter(), List.of(
                              Component.text("Power Switch"),
                              Component.text(world.getConfig().powerSwitch.gold + " Gold").color(NamedTextColor.GOLD)
                      )
        );

    }

    private record SignPosition(Block block, BlockFace direction) { }

    @FunctionalInterface
    private interface SignPositionFetcher {
        Optional<SignPosition> getSignPosition(final World world, final BlockPosition position);
    }

    private Optional<SignPosition> getShopSignPosition(final World world, final BlockPosition position) {
        final Block signBlock = position.toLocation(world).getBlock();
        Block neighbour = null;
        int modX, modZ = 0;
        searchNeighbour:
        for (modX = -1; modX <= 1; modX++) {
            for (modZ = -1 ; modZ <= 1; modZ++) {
                if ((modX == 0) == (modZ == 0)) {
                    continue;
                }
                final Block neighbourCandidate = signBlock.getRelative(modX, 0, modZ);
                if (neighbourCandidate.getType().isEmpty()) {
                    continue;
                }
                neighbour = neighbourCandidate;
                break searchNeighbour;
            }
        }
        if (neighbour == null) {
            return Optional.empty();
        }
        final int finalModX = modX;
        final int finalModZ = modZ;
        final BlockFace directionToNeighbour = Arrays.stream(BlockFace.values())
                .filter(face -> face.getModX() == finalModX && face.getModZ() == finalModZ)
                .findAny().orElseThrow();
        final BlockFace signDirection = directionToNeighbour.getOppositeFace();
        return Optional.of(new SignPosition(signBlock, signDirection));
    }

    private Optional<SignPosition> getPerkSignPosition(final World world, final BlockPosition position) {
        final Block buttonBlock = position.toLocation(world).getBlock();
        if (!(buttonBlock.getBlockData() instanceof final Switch buttonData)) {
            return Optional.empty();
        }
        final BlockFace signDirection = buttonData.getFacing();
        final Block signBlock = buttonBlock.getRelative(BlockFace.UP);
        return Optional.of(new SignPosition(signBlock, signDirection));
    }

        private boolean placeButton(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return false;
        }
        final Block button = signPosition.get().block();
        button.setType(Material.STONE_BUTTON);

        return true;
    }

    private void placeItem(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position,
            final Material material
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return;
        }

        final ItemDisplay item = (ItemDisplay) world.spawnEntity(
                signPosition.get().block.getLocation().toCenterLocation().add(0, -1.5, 0).setDirection(signPosition.get().direction.getDirection()),
                EntityType.ITEM_DISPLAY
        );

        item.setItemStack(new ItemStack(material));
        item.setNoPhysics(true);
        item.setInvulnerable(true);

        final ZombiesEntity entity = new ZombiesEntity(item);
        entity.set(ZombiesEntity.IS_NOT_VANILLA, true);

    }

    private ArmorStand placeArmorStand(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return null;
        }

        final ArmorStand stand = (ArmorStand) world.spawnEntity(
                signPosition.get().block.getLocation().toCenterLocation().add(0, -1.5, 0).setDirection(signPosition.get().direction.getDirection()),
                EntityType.ARMOR_STAND
        );

        stand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        stand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        stand.addEquipmentLock(EquipmentSlot.BODY, ArmorStand.LockType.ADDING_OR_CHANGING);
        stand.setCanTick(false);
        stand.setInvulnerable(true);
        stand.setInvisible(true);
        stand.setNoPhysics(true);
        stand.setGravity(false);

        return stand;
    }

}
