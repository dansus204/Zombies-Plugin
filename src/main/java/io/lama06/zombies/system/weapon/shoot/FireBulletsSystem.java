package io.lama06.zombies.system.weapon.shoot;

import io.lama06.zombies.ZombiesPlugin;
import io.lama06.zombies.event.player.PlayerAttackZombieEvent;
import io.lama06.zombies.event.weapon.WeaponShootEvent;
import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.util.VectorUtil;
import io.lama06.zombies.weapon.ShootData;
import io.lama06.zombies.weapon.Weapon;
import io.lama06.zombies.zombie.Zombie;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class FireBulletsSystem implements Listener {
    @EventHandler
    private void onPlayerAttackEntity(final PrePlayerAttackEntityEvent event) {
        onRightClick(event.getPlayer());
    }

    @EventHandler
    private void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }
        onRightClick(event.getPlayer());
    }

    private void onRightClick(final Player bukkit) {
        final ZombiesPlayer player = new ZombiesPlayer(bukkit);
        if (!player.getWorld().isGameRunning() || !player.isAlive()) {
            return;
        }
        final Weapon weapon = player.getHeldWeapon();
        if (weapon == null) {
            return;
        }
        final ShootData shootData = weapon.getData().shoot;
        if (shootData == null) {
            return;
        }
        final RandomGenerator rnd = ThreadLocalRandom.current();
        final List<WeaponShootEvent.Bullet> bulletsList = new ArrayList<>();
        for (int i = 0; i < shootData.bullets(); i++) {
            final float yaw = (float) (player.getBukkit().getYaw() +
                    (1 - shootData.precision()) * rnd.nextDouble() * 90 * (rnd.nextBoolean() ? 1 : -1));
            final float pitch = (float) (player.getBukkit().getPitch() +
                    (1 - shootData.precision()) * rnd.nextDouble() * 90 * (rnd.nextBoolean() ? 1 : -1));
            final Vector bulletDirection = VectorUtil.fromJawAndPitch(yaw, pitch);
            bulletsList.add(new WeaponShootEvent.Bullet(bulletDirection));
        }
        if (!new WeaponShootEvent(weapon, bulletsList).callEvent()) {
            return;
        }
        if (weapon.getData().sound != null && shootData.delay() == 0) {
            player.playSound(weapon.getData().sound.sound());
        }

        for (int i = 0; i < bulletsList.size(); ++i) {

            final int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(ZombiesPlugin.INSTANCE,
                                                          ()-> {
                                                              detectShotAtZombie(player, weapon, bulletsList.get(finalI));
                                                              if (weapon.getData().sound != null && shootData.delay() != 0) {
                                                                  player.playSound(weapon.getData().sound.sound());
                                                              }
                                                          }
                                                                  ,
                                                          shootData.delay() * i
            );
        }


    }

    private void detectShotAtZombie(final ZombiesPlayer player, final Weapon weapon, final WeaponShootEvent.Bullet bullet) {
        Vector hitPos = null;
        Entity lastEntity = null;
        final int pierceNum = weapon.getData().shoot.pierce();
        for (int i = 0; i < pierceNum; ++i) {
            final Entity finalLastEntity = lastEntity;
            final RayTraceResult ray = player.getWorld().getBukkit().rayTrace(
                    hitPos == null? player.getBukkit().getEyeLocation() : new Location(
                            player.getBukkit().getWorld(), hitPos.getX(), hitPos.getY(), hitPos.getZ()),
                    bullet.direction(),
                    64,
                    FluidCollisionMode.NEVER,
                    true,
                    0,
                    rayTraceEntity -> !rayTraceEntity.equals(player.getBukkit()) &&
                            !(rayTraceEntity.isDead()) && !(rayTraceEntity.equals(finalLastEntity)),
                    rayTraceBlock -> !rayTraceBlock.getType().equals(Material.OAK_SLAB) &&
                            !rayTraceBlock.getType().equals(Material.IRON_BARS)
                    );
            if (ray == null || ray.getHitBlock() != null) {
                return;
            }
            final Entity entity = ray.getHitEntity();
            lastEntity = entity;
            final Zombie zombie = new Zombie(entity);
            if (!zombie.isZombie()) {
                return;
            }
            final double height = entity.getHeight();
            final double pos = entity.getY();
            hitPos = ray.getHitPosition();
            final boolean isCritical = (hitPos.getY() > pos + height*0.8);
            Bukkit.getPluginManager().callEvent(new PlayerAttackZombieEvent(weapon, zombie, isCritical, bullet.direction()));

            int chain = weapon.getData().shoot.chain_reaction();
            if (chain != 0) {
                final List<Entity> entities = new ArrayList<>(List.of(entity));

                while (chain != 0 && entities.getLast() != null) {
                    chain--;
                    final Entity newEntity =
                            entities.getLast().getNearbyEntities(3, 3, 3).stream().filter(
                                    zombiesEntity -> !entities.contains(zombiesEntity) && (new Zombie(zombiesEntity)).isZombie()
                            ).min(Comparator.comparingDouble(
                                    zombieEntity -> zombieEntity.getLocation().distance(entities.getLast().getLocation())
                            )).orElse(null);

                    if (newEntity != null) {
                        Bukkit.getPluginManager().callEvent(
                                new PlayerAttackZombieEvent(
                                        weapon, new Zombie(newEntity), false,
                                        newEntity.getLocation().toVector().subtract(entities.getLast().getLocation().toVector()).normalize()
                                )
                        );
                        entities.add(newEntity);
                    }
                }
            }
        }
    }
}
