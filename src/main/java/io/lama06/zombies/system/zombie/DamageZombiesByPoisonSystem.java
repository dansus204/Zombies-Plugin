package io.lama06.zombies.system.zombie;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.lama06.zombies.ZombiesPlugin;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.zombie.Zombie;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DamageZombiesByPoisonSystem implements Listener {

    @EventHandler
    private void onTick(final ServerTickEndEvent event) {

        for (final ZombiesWorld gameWorld : ZombiesPlugin.INSTANCE.getGameWorlds()) {
            LivingEntity living;
            for (final Zombie zombie : gameWorld.getZombies()) {
                final int poisonTicks = zombie.get(Zombie.POISON_TICKS);
                if (poisonTicks % 10 == 1) {
                    living = (LivingEntity) zombie.getEntity();
                    living.damage(0);
                    living.heal(-1);
                }
                if (poisonTicks > 0) {
                    zombie.set(Zombie.POISON_TICKS, poisonTicks - 1);
                }
            }
        }
    }
}
