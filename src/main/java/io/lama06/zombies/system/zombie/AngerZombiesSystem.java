package io.lama06.zombies.system.zombie;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.lama06.zombies.Window;
import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.ZombiesPlugin;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.event.zombie.ZombieSpawnEvent;
import io.lama06.zombies.zombie.Zombie;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Comparator;

public final class AngerZombiesSystem implements Listener {
    @EventHandler
    private void onServerTick(final ServerTickEndEvent event) {
        for (final ZombiesWorld gameWorld : ZombiesPlugin.INSTANCE.getGameWorlds()) {
            final int angerTimer = gameWorld.get(ZombiesWorld.ANGER_TIMER);
            if (angerTimer > 0) {
                gameWorld.set(ZombiesWorld.ANGER_TIMER, angerTimer - 1);

                continue;
            }
            Mob mob;
            for (final Zombie zombie : gameWorld.getZombies()) {
                mob = (Mob) zombie.getEntity();
                if (zombie.get(Zombie.IN_WINDOW)) {
                    mob.getPathfinder().moveTo(
                            gameWorld.getConfig().graph.getPointLocation(
                                    zombie.get(Zombie.CLOSEST_POINT)
                            )
                    );
                    continue;
                }
                final ZombiesPlayer player = getNearestPlayer(zombie);
                if (player == null) {
                    continue;
                }

                if (calcEffectiveDistance(player.getLocation(), mob.getLocation(), 4) > 100) {
                    final int index = gameWorld.getConfig().graph.tracePath(
                            zombie.get(Zombie.CLOSEST_POINT),
                            player.get(ZombiesPlayer.CLOSEST_POINT),
                            2
                    );
                    mob.getPathfinder().moveTo(
                            gameWorld.getConfig().graph.points[index].location
                    );
                } else {
                    mob.setTarget(player.getBukkit());
                }


            }
            gameWorld.set(ZombiesWorld.ANGER_TIMER, 20);
        }
    }

    private void angerPigZombie(final PigZombie zombie, final ZombiesPlayer nearestPlayer) {
        zombie.setAngry(true);
        zombie.setTarget(nearestPlayer.getBukkit());
    }

    private void angerWolf(final Wolf wolf, final ZombiesPlayer nearestPlayer) {
        wolf.setAngry(true);
        wolf.setTarget(nearestPlayer.getBukkit());
        wolf.setCollarColor(DyeColor.RED);
    }

    private void angerMagmaCube(final MagmaCube magmaCube, final ZombiesPlayer nearestPlayer) {
        magmaCube.setTarget(nearestPlayer.getBukkit());
    }

    private ZombiesPlayer getNearestPlayer(final Zombie zombie) {
        return zombie.getWorld().getAlivePlayers().stream()
                .min(Comparator.comparingDouble(player -> zombie.getWorld().getConfig().graph.finalDistance
                        [zombie.get(Zombie.CLOSEST_POINT)][player.get(ZombiesPlayer.CLOSEST_POINT)]))
                .orElse(null);
    }

    private double calcEffectiveDistance(final Location a, final Location b, double x) {
        final double xDiff = a.getX() - b.getX();
        final double zDiff = a.getZ() - b.getZ();
        final double yDiff = (a.getY() - b.getY()) * x;
        return xDiff*xDiff + yDiff*yDiff + zDiff*zDiff;
    }
}
