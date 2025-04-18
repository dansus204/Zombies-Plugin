package io.lama06.zombies.weapon;

import io.lama06.zombies.menu.MenuDisplayableEnum;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;

public enum WeaponType implements MenuDisplayableEnum {
    KNIFE(
            new WeaponData()
                    .setDisplayName(Component.text("Knife"))
                    .setMaterial(Material.IRON_SWORD)
                    .setMelee(new MeleeData(5))
                    .setAttack(new AttackData(6, false, false, false, 10, 10))
                    .setDelay(new DelayData(10))
    ),
    PISTOL(
            new WeaponData()
                    .setDisplayName(Component.text("Pistol"))
                    .setMaterial(Material.WOODEN_HOE)
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.CRIT))
                    .setAttack(new AttackData(6, false, false, false, 10, 15))
                    .setAmmo(new AmmoData(300, 10))
                    .setDelay(new DelayData(10))
                    .setReload(new ReloadData(30))
                    .setSound(new SoundData(Sound.sound(Key.key("minecraft:entity.iron_golem.hurt"), Sound.Source.BLOCK, 20,2F)))
    ),
    PISTOL_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Pistol Ultimate"))
                    .setMaterial(Material.WOODEN_HOE)
                    .setShoot(new ShootData(2, 3, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.CRIT))
                    .setAttack(new AttackData(6, false, false, false, 10, 15))
                    .setAmmo(new AmmoData(450, 14))
                    .setDelay(new DelayData(8))
                    .setReload(new ReloadData(20))
                    .setEnchanted()
                    .setSound(new SoundData(Sound.sound(Key.key("minecraft:entity.iron_golem.hurt"), Sound.Source.BLOCK, 20,2F)))
    ),

    RIFLE(
            new WeaponData()
                    .setDisplayName(Component.text("Rifle"))
                    .setMaterial(Material.STONE_HOE)
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.END_ROD))
                    .setAttack(new AttackData(6, false, false,  false,  7, 10))
                    .setAmmo(new AmmoData(288, 32))
                    .setDelay(new DelayData(4))
                    .setReload(new ReloadData(30))
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.blast_far"), Sound.Source.BLOCK, 20,2F)))

    ),
    RIFLE_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Rifle Ultimate"))
                    .setMaterial(Material.STONE_HOE)
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.END_ROD))
                    .setAttack(new AttackData(8, false, false,  false,  7, 10))
                    .setAmmo(new AmmoData(315, 35))
                    .setDelay(new DelayData(4))
                    .setReload(new ReloadData(20))
                    .setEnchanted()
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.blast_far"), Sound.Source.BLOCK, 20,2F)))

    ),

    SHOTGUN(
            new WeaponData()
                    .setDisplayName(Component.text("Shotgun"))
                    .setMaterial(Material.IRON_HOE)
                    .setShoot(new ShootData(10, 0, 0.85, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.SMOKE, 1, 1))
                    .setAttack(new AttackData(4.5, false, false, false, 8, 12))
                    .setAmmo(new AmmoData(65, 5))
                    .setDelay(new DelayData(28))
                    .setReload(new ReloadData(30))
                    .setShootParticle(new ShootParticleData(Particle.SMOKE))
                    .setSound(new SoundData(Sound.sound(Key.key("entity.generic.explode"), Sound.Source.BLOCK, 20,2F)))


    ),
    SHOTGUN_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Shotgun Ultimate"))
                    .setMaterial(Material.IRON_HOE)
                    .setShoot(new ShootData(10, 0, 0.85, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.SMOKE, 1, 1))
                    .setAttack(new AttackData(4.5, false, false, false, 8, 12))
                    .setAmmo(new AmmoData(80, 5))
                    .setDelay(new DelayData(20))
                    .setReload(new ReloadData(20))
                    .setEnchanted()
                    .setShootParticle(new ShootParticleData(Particle.SMOKE))
                    .setSound(new SoundData(Sound.sound(Key.key("entity.generic.explode"), Sound.Source.BLOCK, 20,2F)))


    ),

    SNIPER(
            new WeaponData()
                    .setDisplayName(Component.text("Sniper"))
                    .setMaterial(Material.WOODEN_SHOVEL)
                    .setShoot(new ShootData(1, 0, 1, 2, 0, 0))
                    .setAttack(new AttackData(30, false, false, false, 30, 45))
                    .setAmmo(new AmmoData(40, 4))
                    .setDelay(new DelayData(20))
                    .setReload(new ReloadData(30))
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.blast"), Sound.Source.BLOCK, 0.25F,1F)))

    ),
    SNIPER_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Sniper Ultimate"))
                    .setMaterial(Material.WOODEN_SHOVEL)
                    .setShoot(new ShootData(1, 0, 1, 4, 0, 0))
                    .setAttack(new AttackData(40, false, false, false, 30, 45))
                    .setAmmo(new AmmoData(60, 5))
                    .setDelay(new DelayData(20))
                    .setReload(new ReloadData(30))
                    .setEnchanted()
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.blast"), Sound.Source.BLOCK, 0.25F,1F)))

    ),
    FLAME_THROWER(
            new WeaponData()
                    .setDisplayName(Component.text("Flame Thrower"))
                    .setMaterial(Material.GOLDEN_HOE)
                    .setShoot(new ShootData(1, 0, 0.95, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.FLAME))
                    .setAttack(new AttackData(2, true, false, false,  4, 6))
                    .setAmmo(new AmmoData(350, 50))
                    .setDelay(new DelayData(2))
                    .setReload(new ReloadData(30))
                    .includeInLuckyChest()
    ),
    GOLD_DIGGER(
            new WeaponData()
                    .setDisplayName(Component.text("Gold Digger").color(NamedTextColor.GOLD))
                    .setMaterial(Material.GOLDEN_PICKAXE)
                    .setAttack(new AttackData(6, false, false, false, 10, 15))
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.FLAME))
                    .setDelay(new DelayData(10))
                    .setReload(new ReloadData(30))
                    .setAmmo(new AmmoData(70, 7))
                    .includeInLuckyChest()
    ),
    GOLD_DIGGER_ULTIMATE_1(
            new WeaponData()
                    .setDisplayName(Component.text("Gold Digger Ultimate I").color(NamedTextColor.GOLD))
                    .setMaterial(Material.GOLDEN_PICKAXE)
                    .setAttack(new AttackData(8, false, false, false, 10, 15))
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.FLAME))
                    .setDelay(new DelayData(10))
                    .setReload(new ReloadData(28))
                    .setAmmo(new AmmoData(100, 10))
                    .setEnchanted()
    ),
    GOLD_DIGGER_ULTIMATE_2(
            new WeaponData()
                    .setDisplayName(Component.text("Gold Digger Ultimate II").color(NamedTextColor.GOLD))
                    .setMaterial(Material.GOLDEN_PICKAXE)
                    .setAttack(new AttackData(10, false, false, false, 10, 15))
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.FLAME))
                    .setDelay(new DelayData(8))
                    .setReload(new ReloadData(26))
                    .setAmmo(new AmmoData(130, 13))
                    .setEnchanted()
    ),
    GOLD_DIGGER_ULTIMATE_3(
            new WeaponData()
                    .setDisplayName(Component.text("Gold Digger Ultimate III").color(NamedTextColor.GOLD))
                    .setMaterial(Material.GOLDEN_PICKAXE)
                    .setAttack(new AttackData(12, false, false, false, 10, 15))
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.FLAME))
                    .setDelay(new DelayData(6))
                    .setReload(new ReloadData(24))
                    .setAmmo(new AmmoData(160, 16))
                    .setEnchanted()
    ),
    GOLD_DIGGER_ULTIMATE_4(
            new WeaponData()
                    .setDisplayName(Component.text("Gold Digger Ultimate IV").color(NamedTextColor.GOLD))
                    .setMaterial(Material.GOLDEN_PICKAXE)
                    .setAttack(new AttackData(15, false, false, false, 10, 15))
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(null)
                    .setDelay(new DelayData(6))
                    .setReload(new ReloadData(22))
                    .setAmmo(new AmmoData(200, 20))
                    .setEnchanted()
    ),
    GOLD_DIGGER_ULTIMATE_5(
            new WeaponData()
                    .setDisplayName(Component.text("Gold Digger Ultimate V").color(NamedTextColor.GOLD))
                    .setMaterial(Material.GOLDEN_PICKAXE)
                    .setAttack(new AttackData(20, false, false, false, 10, 15))
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setShootParticle(null)
                    .setDelay(new DelayData(5))
                    .setReload(new ReloadData(20))
                    .setAmmo(new AmmoData(250, 25))
                    .setEnchanted()
    ),

    ELDER_GUN(
            new WeaponData()
                    .setDisplayName(Component.text("Elder Gun").color(NamedTextColor.GOLD))
                    .setMaterial(Material.SHEARS)
                    .setAttack(new AttackData(15, false, true, false, 20, 30))
                    .setShoot(new ShootData(1, 0, 1, 100, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.TOTEM_OF_UNDYING))
                    .setDelay(new DelayData(18))
                    .setReload(new ReloadData(40))
                    .setAmmo(new AmmoData(60, 4))
                    .includeInLuckyChest()

    ),
    ELDER_GUN_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Elder Gun Ultimate").color(NamedTextColor.GOLD))
                    .setMaterial(Material.SHEARS)
                    .setAttack(new AttackData(20, false, true, false, 20, 30))
                    .setShoot(new ShootData(1, 0, 1, 100, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.TOTEM_OF_UNDYING))
                    .setDelay(new DelayData(16))
                    .setReload(new ReloadData(40))
                    .setAmmo(new AmmoData(100, 4))
                    .setEnchanted()
    ),
    DOUBLE_BARREL_SHOTGUN(
            new WeaponData()
                    .setDisplayName(Component.text("Double Barrel Shotgun"))
                    .setMaterial(Material.FLINT_AND_STEEL)
                    .setShoot(new ShootData(10, 0, 0.85, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.ASH))
                    .setAttack(new AttackData(7, false, false, false, 8, 12))
                    .setAmmo(new AmmoData(20, 2))
                    .setDelay(new DelayData(6))
                    .setReload(new ReloadData(60))
    ),
    DOUBLE_BARREL_SHOTGUN_ULTIMATE_1(
            new WeaponData()
                    .setDisplayName(Component.text("Double Barrel Shotgun Ultimate I"))
                    .setMaterial(Material.FLINT_AND_STEEL)
                    .setShoot(new ShootData(10, 0, 0.85, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.ASH))
                    .setAttack(new AttackData(7, false, false, false, 8, 12))
                    .setAmmo(new AmmoData(30, 2))
                    .setDelay(new DelayData(6))
                    .setReload(new ReloadData(50))
                    .setEnchanted()
    ),
    DOUBLE_BARREL_SHOTGUN_ULTIMATE_2(
            new WeaponData()
                    .setDisplayName(Component.text("Double Barrel Shotgun Ultimate II"))
                    .setMaterial(Material.FLINT_AND_STEEL)
                    .setShoot(new ShootData(10, 0, 0.85, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.ANGRY_VILLAGER))
                    .setAttack(new AttackData(8, false, false, false, 8, 12))
                    .setAmmo(new AmmoData(36, 2))
                    .setDelay(new DelayData(6))
                    .setReload(new ReloadData(50))
                    .setEnchanted()
    ),
    DOUBLE_BARREL_SHOTGUN_ULTIMATE_3(
            new WeaponData()
                    .setDisplayName(Component.text("Double Barrel Shotgun Ultimate III"))
                    .setMaterial(Material.FLINT_AND_STEEL)
                    .setShoot(new ShootData(10, 0, 0.85, 1, 0, 0))
                    .setShootParticle(new ShootParticleData(Particle.ANGRY_VILLAGER))
                    .setAttack(new AttackData(8, false, false, false, 8, 12))
                    .setAmmo(new AmmoData(42, 2))
                    .setDelay(new DelayData(6))
                    .setReload(new ReloadData(46))
                    .setEnchanted()
    ),
    ZOMBIE_ZAPPER(
            new WeaponData()
                    .setDisplayName(Component.text("Zombie Zapper"))
                    .setMaterial(Material.DIAMOND_PICKAXE)
                    .setShoot(new ShootData(1, 0, 1, 1, 3, 0))
                    .setShootParticle(new ShootParticleData(Particle.ENCHANTED_HIT))
                    .setAttack(new AttackData(12, false, false, false, 15, 20))
                    .setAmmo(new AmmoData(100, 10))
                    .setDelay(new DelayData(10))
                    .setReload(new ReloadData(40))
                    .includeInLuckyChest()
                    .setSound(new SoundData(Sound.sound(Key.key("block.note_block.hat"), Sound.Source.BLOCK, 20,1F)))

    ),
    ZOMBIE_ZAPPER_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Zombie Zapper Ultimate"))
                    .setMaterial(Material.DIAMOND_PICKAXE)
                    .setShoot(new ShootData(1, 0, 1, 1, 4, 0))
                    .setShootParticle(new ShootParticleData(Particle.ENCHANTED_HIT))
                    .setAttack(new AttackData(18, false, false, false, 15, 20))
                    .setAmmo(new AmmoData(120, 10))
                    .setDelay(new DelayData(10))
                    .setReload(new ReloadData(30))
                    .setEnchanted()
                    .setSound(new SoundData(Sound.sound(Key.key("block.note_block.hat"), Sound.Source.BLOCK, 20,1F)))

    ),

    BLOW_DART(
            new WeaponData()
                    .setDisplayName(Component.text("Blow Dart"))
                    .setMaterial(Material.IRON_SHOVEL)
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setAttack(new AttackData(10, false, false, false, 20, 30))
                    .setAmmo(new AmmoData(100, 10))
                    .setDelay(new DelayData(10))
                    .setReload(new ReloadData(60))
                    .includeInLuckyChest()
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.shoot"), Sound.Source.BLOCK, 20,1F)))
                    .setPoisoned()
    ),
    BLOW_DART_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Blow Dart Ultimate"))
                    .setMaterial(Material.IRON_SHOVEL)
                    .setEnchanted()
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setAttack(new AttackData(10, false, false, false, 20, 30))
                    .setAmmo(new AmmoData(150, 10))
                    .setDelay(new DelayData(6))
                    .setReload(new ReloadData(36))
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.shoot"), Sound.Source.BLOCK, 20,1F)))
                    .setPoisoned()
    ),
    ZOMBIE_SOAKER(
            new WeaponData()
                    .setDisplayName(Component.text("Zombie Soaker"))
                    .setMaterial(Material.DIAMOND_HOE)
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setAttack(new AttackData(5, false, false, false, 5, 10))
                    .setAmmo(new AmmoData(256, 32))
                    .setDelay(new DelayData(5))
                    .setReload(new ReloadData(30))
                    .includeInLuckyChest()
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.shoot"), Sound.Source.BLOCK, 20,1F)))
    ),
    ZOMBIE_SOAKER_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Zombie Soaker Ultimate"))
                    .setMaterial(Material.DIAMOND_HOE)
                    .setEnchanted()
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 0))
                    .setAttack(new AttackData(8, false, false, false, 5, 10))
                    .setAmmo(new AmmoData(320, 32))
                    .setDelay(new DelayData(5))
                    .setReload(new ReloadData(20))
                    .includeInLuckyChest()
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket.shoot"), Sound.Source.BLOCK, 20,1F)))
    ),
    ROCKET_LAUNCHER(
            new WeaponData()
                    .setDisplayName(Component.text("Rocket Launcher"))
                    .setMaterial(Material.STONE_SHOVEL)
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 1))
                    .setAttack(new AttackData(6.5, false, false, false, 0, 30))
                    .setAmmo(new AmmoData(20, 4))
                    .setDelay(new DelayData(40))
                    .setReload(new ReloadData(60))
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket."), Sound.Source.BLOCK, 20,1F)))
    ),
    ROCKET_LAUNCHER_ULTIMATE(
            new WeaponData()
                    .setDisplayName(Component.text("Rocket Launcher Ultimate"))
                    .setMaterial(Material.STONE_SHOVEL)
                    .setEnchanted()
                    .setShoot(new ShootData(1, 0, 1, 1, 0, 1))
                    .setAttack(new AttackData(9, false, false, false, 0, 30))
                    .setAmmo(new AmmoData(32, 4))
                    .setDelay(new DelayData(30))
                    .setReload(new ReloadData(40))
                    .setSound(new SoundData(Sound.sound(Key.key("entity.firework_rocket."), Sound.Source.BLOCK, 20,1F)))
    ),



    ;

    public final WeaponData data;

    WeaponType(final WeaponData data) {
        this.data = data;
    }

    @Override
    public Component getDisplayName() {
        return data.displayName;
    }

    @Override
    public Material getDisplayMaterial() {
        return data.material;
    }
}
