package io.lama06.zombies.system.lucky_chest;

import io.lama06.zombies.LuckyChest;
import io.lama06.zombies.ZombiesPlugin;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.event.player.PlayerGoldChangeEvent;
import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.util.pdc.EnumPersistentDataType;
import io.lama06.zombies.weapon.WeaponType;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class InteractWithLuckyChestSystem implements Listener {
    @EventHandler
    private void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.getAction().isLeftClick()) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        final BlockPosition clickedBlockPos = clickedBlock.getLocation().toBlock();
        final ZombiesPlayer player = new ZombiesPlayer(event.getPlayer());
        final ZombiesWorld world = player.getWorld();
        if (!world.isGameRunning()) {
            return;
        }
        final LuckyChest chest = world.getConfig().luckyChests.stream()
                .filter(luckyChest -> {
                    final boolean first = luckyChest.position.equals(clickedBlockPos);
                    final Block secondBlock = luckyChest.getSecondChestBlock(world.getBukkit());
                    final boolean second = secondBlock != null && secondBlock.equals(clickedBlock);
                    return first || second;
                })
                .findAny().orElse(null);
        if (chest == null) {
            return;
        }
        player.getBukkit().closeInventory();
        if (chest.uses_left == 0) {
            player.sendMessage(Component.text("This lucky chest is not active. Use active lucky chest in " + Objects.requireNonNull(world.getConfig().luckyChests.stream().filter(luckyChest -> luckyChest.uses_left != 0).findAny().orElse(null)).area + ".")
                                       .color(NamedTextColor.RED));
            return;
        }

        if (chest.state == 1) {
            return;
        }

        if (chest.state == 0) {
            openLuckyChest(player, world, chest);
        }

        if (chest.state == 2) {
            claimItem(player, chest);
        }

    }

    private void decrementChestUses(final LuckyChest chest, final ZombiesWorld world) {
        if (--chest.uses_left == 0) {
            world.setNewLuckyChest(chest);
            world.sendMessage(Component.text("Lucky Chest has moved to a new location!"));
        } else {
            for (final TextDisplay display : chest.textDisplays) {
                display.setVisibleByDefault(true);
            }
        }
    }

    private void openLuckyChest(final ZombiesPlayer player, final ZombiesWorld world, final LuckyChest chest) {
        final int gold = player.get(ZombiesPlayer.GOLD);
        if (gold < chest.gold) {
            player.sendMessage(Component.text("You cannot afford this"));
            return;
        }


        final FinePosition itemPosition = chest.getItemPosition(world.getBukkit());

        for (final TextDisplay display : chest.textDisplays) {
            display.setVisibleByDefault(false);
        }

        final ItemDisplay itemDisplay = (ItemDisplay) world.getBukkit().spawnEntity(itemPosition.toLocation(world.getBukkit()), EntityType.ITEM_DISPLAY);
        itemDisplay.setCustomNameVisible(true);
        chest.display = itemDisplay;

        final int newGold = gold - chest.gold;
        player.set(ZombiesPlayer.GOLD, newGold);
        Bukkit.getPluginManager().callEvent(new PlayerGoldChangeEvent(player, gold, newGold));

        chest.state = 1;

        final List<WeaponType> weaponTypes = Arrays.stream(WeaponType.values())
                .filter(weaponType -> weaponType.data.inLuckyChest).toList();
        final RandomGenerator rnd = ThreadLocalRandom.current();
        final PersistentDataContainer pdc = itemDisplay.getPersistentDataContainer();

        for (int i = 0; i < 14; ++i) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(ZombiesPlugin.INSTANCE, () -> {
                final WeaponType weaponType = weaponTypes.get(rnd.nextInt(weaponTypes.size()));
                itemDisplay.setItemStack(new ItemStack(weaponType.getDisplayMaterial()));
                itemDisplay.customName(weaponType.getDisplayName());
                pdc.set(LuckyChestItem.getWeaponKey(), new EnumPersistentDataType<>(WeaponType.class), weaponType);

            }, i * 10);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZombiesPlugin.INSTANCE, () -> {
            chest.state = 2;
            final WeaponType weaponType = pdc.get(LuckyChestItem.getWeaponKey(), new EnumPersistentDataType<>(WeaponType.class));
            player.sendMessage(Component.text("You found ")
                                       .append(weaponType.getDisplayName())
                                       .append(Component.text(" in Lucky Chest! Claim it before it disappears in 15 seconds!")));
            world.getPlayers().forEach(zombiesPlayer -> {
                                           if (!zombiesPlayer.getBukkit().equals(player.getBukkit())) {
                                               zombiesPlayer.sendMessage(
                                                       player.getBukkit().name().append(Component.text(" found ")).append(weaponType.getDisplayName()).append(Component.text(" in Lucky Chest!")));
                                           }
                                       }
            );


            final TextDisplay textDisplay = (TextDisplay) world.getBukkit().spawnEntity(itemPosition.toLocation(world.getBukkit()).add(0, -0.25, 0), EntityType.TEXT_DISPLAY);
            textDisplay.setBillboard(Display.Billboard.CENTER);
            chest.timer = textDisplay;

            for (int i = 0; i < 150; ++i) {
                final int finalI = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(ZombiesPlugin.INSTANCE, () -> {
                    textDisplay.text(Component.text(new String("%.1f").formatted(15 - finalI * 0.1)));
                }, i * 2);
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(ZombiesPlugin.INSTANCE, () -> {
                if (chest.state != 0) {
                    player.sendMessage(Component.text("Your item has disappeared!"));
                    chest.state = 0;
                    textDisplay.remove();
                    itemDisplay.remove();
                    decrementChestUses(chest, world);
                }
            }, 300);
        }, 140);
    }



    private void claimItem(final ZombiesPlayer player, final LuckyChest chest) {
        final ItemDisplay itemDisplay = chest.display;
        if (itemDisplay == null) {
            return;
        }
        final PersistentDataContainer pdc = itemDisplay.getPersistentDataContainer();
        final WeaponType weaponType = pdc.get(LuckyChestItem.getWeaponKey(), new EnumPersistentDataType<>(WeaponType.class));
        if (weaponType == null) {
            return;
        }
        final PlayerInventory inventory = player.getBukkit().getInventory();
        final int slot = inventory.getHeldItemSlot();
        if (slot == 0 || slot > player.getLastWeaponSlot()) {
            player.sendMessage(Component.text("Select a weapon slot").color(NamedTextColor.RED));
            return;
        }
        chest.state = 0;
        chest.timer.remove();
        itemDisplay.remove();
        player.giveWeapon(slot, weaponType);
        decrementChestUses(chest, player.getWorld());
    }
}
