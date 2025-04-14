package io.lama06.zombies.zombie;

import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ZombieData {
    public EntityType entity;
    public int health;
    public Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
    public BreakWindowData breakWindow;
    public boolean fireImmune;
    public boolean kbImmune;
    public boolean isBoss;
    public LaserAttackData laserAttack;
    public FireAttackData fireAttack;
    public ExplosionAttackData explosionAttack;
    public boolean fireTrail;
    public PotionEffect potionEffect;
    public DescendantsData descendants;
    public FireBallAttackData fireBallAttack;
    public Consumer<? super Entity> initializer;

    public ZombieData addPotionEffect(final PotionEffect potionEffect) {
        this.potionEffect = potionEffect;
        return this;
    }

    public ZombieData setEntity(final EntityType entity) {
        this.entity = entity;
        return this;
    }

    public ZombieData setHealth(final int health) {
        this.health = health;
        return this;
    }

    public ZombieData addEquipment(final EquipmentSlot slot, final ItemStack item, final Color color) {
        if (color != null) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        equipment.put(slot, item);
        return this;
    }

    public ZombieData setBreakWindow(final BreakWindowData breakWindow) {
        this.breakWindow = breakWindow;
        return this;
    }

    public ZombieData setFireImmune(final boolean fireImmune) {
        this.fireImmune = fireImmune;
        return this;
    }

    public ZombieData setKbImmune(final boolean kbImmune) {
        this.kbImmune = kbImmune;
        return this;
    }

    public ZombieData setBoss(final boolean isBoss) {
        this.isBoss = isBoss;
        return this;
    }

    public ZombieData setLaserAttack(final LaserAttackData laserAttack) {
        this.laserAttack = laserAttack;
        return this;
    }

    public ZombieData setFireAttack(final FireAttackData fireAttack) {
        this.fireAttack = fireAttack;
        return this;
    }

    public ZombieData setExplosionAttack(final ExplosionAttackData explosionAttack) {
        this.explosionAttack = explosionAttack;
        return this;
    }

    public ZombieData setFireTrail(final boolean fireTrail) {
        this.fireTrail = fireTrail;
        return this;
    }

    public ZombieData setDescendants(final DescendantsData descendants) {
        this.descendants = descendants;
        return this;
    }

    public ZombieData setFireBallAttack(final FireBallAttackData fireBallAttack) {
        this.fireBallAttack = fireBallAttack;
        return this;
    }

    public ZombieData setInitializer(final Consumer<? super Entity> initializer) {
        this.initializer = initializer;
        return this;
    }
}
