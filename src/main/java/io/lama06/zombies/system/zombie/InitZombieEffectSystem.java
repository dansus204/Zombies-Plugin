package io.lama06.zombies.system.zombie;

import io.lama06.zombies.event.zombie.ZombieSpawnEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;


public final class InitZombieEffectSystem implements Listener {
    @EventHandler
    private void onSpawn(final ZombieSpawnEvent event) {
        final Entity entity = event.getZombie().getEntity();
        if (!(entity instanceof final LivingEntity living)) {
            return;
        }
        final PotionEffect effect = event.getData().potionEffect;
        if (effect == null) {
            return;
        }
        living.addPotionEffect(effect);
    }
}