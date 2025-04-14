package io.lama06.zombies.system.zombie;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.lama06.zombies.ZombiesPlugin;
import io.lama06.zombies.ZombiesWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class UpdateBossHpBarSystem implements Listener {
    /*@EventHandler
    private void onServerTick(final ServerTickEndEvent event) {
        for (final ZombiesWorld gameWorld : ZombiesPlugin.INSTANCE.getGameWorlds()) {
            final int timer = gameWorld.get(ZombiesWorld.HP_BAR_TIMER);
            if (timer > 0) {
                gameWorld.set(ZombiesWorld.HP_BAR_TIMER, timer - 1);
                continue;
            }


            gameWorld.set(ZombiesWorld.HP_BAR_TIMER, 20);
        }
    } */
}

