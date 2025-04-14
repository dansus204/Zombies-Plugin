package io.lama06.zombies.system;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.ZombiesPlugin;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.zombie.Zombie;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UpdateGraphIndexSystem implements Listener {
    @EventHandler
    private void onServerTick(final ServerTickEndEvent event) {
        for (final ZombiesWorld world : ZombiesPlugin.INSTANCE.getGameWorlds()) {
            final int timer = world.get(ZombiesWorld.INDEX_UPDATE_TIMER);
            if (timer > 0) {
                world.set(ZombiesWorld.INDEX_UPDATE_TIMER, timer - 1);
                continue;
            }
            for (final Zombie zombie : world.getZombies()) {
                zombie.set(Zombie.CLOSEST_POINT,
                           world.getConfig().graph.updateIndex(zombie.getEntity().getLocation(),
                                                                zombie.get(Zombie.CLOSEST_POINT))
                           );
            }
            for (final ZombiesPlayer player : world.getPlayers()) {
                if (player == null) {
                    continue;
                }
                player.set(ZombiesPlayer.CLOSEST_POINT,
                           world.getConfig().graph.updateIndex(player.getBukkit().getLocation(),
                                                               player.get(ZombiesPlayer.CLOSEST_POINT)));
            }
            world.set(ZombiesWorld.INDEX_UPDATE_TIMER, 10);
        }
    }
}
