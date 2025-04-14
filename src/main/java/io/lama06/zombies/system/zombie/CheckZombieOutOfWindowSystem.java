package io.lama06.zombies.system.zombie;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.lama06.zombies.ZombiesPlugin;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.zombie.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class CheckZombieOutOfWindowSystem implements Listener {
    @EventHandler
    private void onServerTick(final ServerTickEndEvent event) {
        for (final ZombiesWorld gameWorld : ZombiesPlugin.INSTANCE.getGameWorlds()) {
            final int checkTimer = gameWorld.get(ZombiesWorld.CHECK_TIMER);
            if (checkTimer > 0) {
                gameWorld.set(ZombiesWorld.CHECK_TIMER, checkTimer - 1);
                continue;
            }
            for (final Zombie zombie : gameWorld.getZombies()) {
                if (!(zombie.get(Zombie.IN_WINDOW))) {
                    continue;
                }
                if (zombie.get(Zombie.SPAWN_POS_Y) - zombie.getEntity().getY() > 0.9) {
                    zombie.set(Zombie.IN_WINDOW, false);
                }
            }
            gameWorld.set(ZombiesWorld.CHECK_TIMER, 10);
        }
    }

}
