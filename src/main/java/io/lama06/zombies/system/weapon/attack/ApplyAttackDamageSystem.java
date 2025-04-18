package io.lama06.zombies.system.weapon.attack;

import io.lama06.zombies.event.player.PlayerAttackZombieEvent;
import io.lama06.zombies.weapon.AttackData;
import io.lama06.zombies.weapon.Weapon;
import io.lama06.zombies.weapon.WeaponType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ApplyAttackDamageSystem implements Listener {
    @EventHandler
    private void onPlayerAttackZombie(final PlayerAttackZombieEvent event) {
        final WeaponType weaponType = event.getWeaponType();
        final AttackData attackData = weaponType.data.attack;

        if (attackData == null) {
            return;
        }
        event.setBaseDamage(attackData.damage());
        if (event.getZombie().getData().fireImmune) {
            if (weaponType == WeaponType.ZOMBIE_SOAKER) {
                event.applyDamageModifier(3);
            }
            if (weaponType == WeaponType.ZOMBIE_SOAKER_ULTIMATE) {
                event.applyDamageModifier(4);
            }
        }
        if (attackData.fire()) {
            event.setFire(true);
        }
    }
}
