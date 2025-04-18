package io.lama06.zombies.system.weapon.shoot;

import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.event.player.PlayerAttackZombieEvent;
import io.lama06.zombies.weapon.WeaponType;
import io.lama06.zombies.zombie.Zombie;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class FireballExplosionSystem implements Listener {

    @EventHandler
    private void onFireballCollision(final ProjectileHitEvent event) {
        final WeaponType weapon = new ZombiesFireball(event.getEntity()).get(ZombiesFireball.FIREBALL_WEAPON);
        if (weapon == null) {
            return;
        }
        final Entity hitEntity = event.getHitEntity();
        final Zombie zombie = new Zombie(hitEntity);
        if (hitEntity != null && !zombie.isZombie()) {
            event.setCancelled(true);
            return;
        }
        final Fireball fireball = (Fireball) event.getEntity();
        final ZombiesPlayer player = new ZombiesPlayer((Player) fireball.getShooter());
        if (hitEntity != null) {
            Bukkit.getPluginManager().callEvent(
                    new PlayerAttackZombieEvent(
                            weapon, player, new Zombie(hitEntity) , true, fireball.getLocation().getDirection()
                    )
            );
        }
        fireball.getNearbyEntities(3, 3, 3).stream()
                .filter(entity -> entity.getLocation().distance(fireball.getLocation()) < 3 && new Zombie(entity).isZombie())
                .forEach(entity -> Bukkit.getPluginManager().callEvent(
                        new PlayerAttackZombieEvent(
                                weapon, player, new Zombie(entity), false,
                                entity.getLocation().subtract(fireball.getLocation()).toVector().normalize()
                        )
                ));
    }
}
