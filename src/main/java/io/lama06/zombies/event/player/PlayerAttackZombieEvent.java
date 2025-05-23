package io.lama06.zombies.event.player;

import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.util.HandlerListGetter;
import io.lama06.zombies.weapon.Weapon;
import io.lama06.zombies.weapon.WeaponType;
import io.lama06.zombies.zombie.Zombie;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public final class PlayerAttackZombieEvent extends PlayerEvent {
    public static final HandlerList HANDLERS = new HandlerList();

    @HandlerListGetter
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final WeaponType weaponType;
    private final Zombie zombie;
    private boolean fire;
    private boolean kill;
    private boolean freeze;
    private double baseDamage;
    private double damageModifier = 1;
    private boolean critical;
    private Vector direction;

    public PlayerAttackZombieEvent(final WeaponType weaponType, final ZombiesPlayer player, final Zombie zombie, final boolean critical,
                                   final Vector direction
    ) {
        super(player);
        this.zombie = zombie;
        this.critical = critical;
        this.weaponType = weaponType;
        this.direction = direction;
    }

    public Zombie getZombie() {
        return zombie;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setBaseDamage(final double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public void applyDamageModifier(final double damageModifier) {
        this.damageModifier *= damageModifier;
    }

    public double getDamage() {
        return baseDamage * damageModifier;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(final boolean fire) {
        this.fire = fire;
    }

    public boolean isKill() {
        return kill;
    }

    public void setKill(final boolean kill) {
        this.kill = kill;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void setFreeze(final boolean freeze) {
        this.freeze = freeze;
    }

    public boolean isCritical() { return critical; }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(final Vector direction) {
        this.direction = direction;
    }
}
