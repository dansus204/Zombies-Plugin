package io.lama06.zombies.zombie;

import io.lama06.zombies.util.PlayerHeads;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum ZombieType {
    ZOMBIE1(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.WOODEN_AXE), null)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE2(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLACK)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.WOODEN_AXE), null)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE3(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.WOODEN_AXE), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLACK)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.BLACK)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE4(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.STONE_AXE), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLACK)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.BLACK)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE5(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.STONE_AXE), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.CHAINMAIL_CHESTPLATE), null)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.BLACK)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE6(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.IRON_AXE), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.CHAINMAIL_CHESTPLATE), null)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.BLACK)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE7(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.IRON_AXE), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.CHAINMAIL_CHESTPLATE), null)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS), null)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS), null)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE8(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.IRON_AXE), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE), null)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS), null)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS), null)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE9(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.IRON_AXE), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE), null)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.IRON_BOOTS), null)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS), null)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    ZOMBIE_ANGRY(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE), Color.RED)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.RED)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_AXE), null)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.RED)
                    .addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1))
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(20)
    ),
    PIG_ZOMBIE(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIFIED_PIGLIN)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.GOLDEN_CHESTPLATE), null)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.GOLDEN_BOOTS), null)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.GOLDEN_LEGGINGS), null)
                    .setHealth(20)
                    .setBreakWindow(new BreakWindowData(20))
                    .setFireImmune(true)
    ),
    MAGMA_CUBE(
            new ZombieData()
                    .setEntity(EntityType.MAGMA_CUBE)
                    .setHealth(3)
                    .setFireImmune(true)
                    .setInitializer(entity -> ((MagmaCube) entity).setSize(2))
    ),
    MAGMA_ZOMBIE(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(7)
                    .setFireImmune(true)
                    .setDescendants(new DescendantsData(ZombieType.MAGMA_CUBE, 3))
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.GOLDEN_SWORD), null)
                    .addEquipment(EquipmentSlot.HEAD, PlayerHeads.MAGMA_CUBE.createItem(), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE), Color.ORANGE)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.ORANGE)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.ORANGE)
    ),
    LITTLE_BOMBIE(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .setBreakWindow(new BreakWindowData(20))
                    .setHealth(10)
                    .setExplosionAttack(ExplosionAttackData.explodeOnDeath(4))
                    .addEquipment(EquipmentSlot.HEAD, new ItemStack(Material.TNT), null)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLACK)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.BLACK)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK)
                    .setInitializer(entity -> ((Zombie) entity).setBaby())
    ),
    FIRE_ZOMBIE(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .setHealth(20)
                    .addEquipment(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE), Color.ORANGE)
                    .addEquipment(EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS), Color.ORANGE)
                    .addEquipment(EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS), Color.ORANGE)
                    .setBreakWindow(new BreakWindowData(20))
                    .setFireImmune(true)
                    .setFireAttack(new FireAttackData(3*20))
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.BLAZE_ROD), null)
    ),
    ZOMBIE_WOLF(
            new ZombieData()
                    .setEntity(EntityType.WOLF)
                    .setHealth(20)
                    .setBreakWindow(new BreakWindowData(20))
                    .setInitializer(entity -> ((Wolf) entity).setAngry(true))
    ),
    GUARDIAN_ZOMBIE(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .setHealth(13)
                    .setBreakWindow(new BreakWindowData(2*20))
                    .addEquipment(EquipmentSlot.HEAD, new ItemStack(Material.SEA_LANTERN), null)
                    .setLaserAttack(new LaserAttackData(3))
    ),
    INFERNO(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .setHealth(100)
                    .addEquipment(EquipmentSlot.HAND, new ItemStack(Material.BLAZE_ROD), null)
                    .setFireTrail(true)
                    .setBreakWindow(new BreakWindowData(20))
                    .setFireImmune(true)
                    .setFireBallAttack(new FireBallAttackData(4, 40))
                    .setFireAttack(new FireAttackData(40))
                    .setKbImmune(true)
                    .setBoss(true)
    ),
    BOMBIE(
            new ZombieData()
                    .setEntity(EntityType.ZOMBIE)
                    .setHealth(100)
                    .setExplosionAttack(ExplosionAttackData.explodePeriodically(2*20 + 10, 4))
                    .setBreakWindow(new BreakWindowData(20))
                    .setKbImmune(true)
                    .setBoss(true)
    );

    public final ZombieData data;

    ZombieType(final ZombieData data) {
        this.data = data;
    }
}
