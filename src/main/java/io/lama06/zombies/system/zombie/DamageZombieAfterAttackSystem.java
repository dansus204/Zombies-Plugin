package io.lama06.zombies.system.zombie;

import io.lama06.zombies.event.player.PlayerAttackZombieEvent;
import io.lama06.zombies.zombie.Zombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class DamageZombieAfterAttackSystem implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onAttack(final PlayerAttackZombieEvent event) {
        final Zombie zombie = event.getZombie();
        final Entity entity = zombie.getEntity();
        if (event.isFire()) {
            entity.setFireTicks(5 * 20);
        }
        if (!(entity instanceof final LivingEntity living)) {
            return;
        }
        if (event.isKill()) {
            living.setKiller(event.getPlayer().getBukkit());
            living.setHealth(0);
            return;
        }
        living.setNoDamageTicks(0);
        final double damage = event.getDamage();
        final Vector velocity = living.getVelocity();
        if (event.isCritical()) {
            living.damage(0, event.getPlayer().getBukkit());
            final double hp = living.getHealth();
            living.setHealth(hp > damage? hp - damage : 0);
        } else {
            living.damage(damage, event.getPlayer().getBukkit());
        }
        if (event.getDirection() != null) {
            living.setVelocity(event.getDirection());
        }
        if (zombie.getData().kbImmune) {
            living.setVelocity(velocity);
        }
        if (event.isFreeze()) {
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 2));
        }
    }
}
