package io.lama06.zombies.system;

import io.lama06.zombies.ArmorShop;
import io.lama06.zombies.ZombiesEntity;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.event.player.PlayerGoldChangeEvent;
import io.lama06.zombies.ZombiesPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public final class BuyArmorAtShopSystem implements Listener {
    @EventHandler
    private void onPlayerInteract(final PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND) {
            return;
        }

        final ZombiesPlayer player = new ZombiesPlayer(event.getPlayer());
        final ZombiesWorld world = player.getWorld();
        if (!world.isGameRunning()) {
            return;
        }
        final ZombiesEntity entity = new ZombiesEntity(event.getRightClicked());
        if (!entity.get(ZombiesEntity.IS_NOT_VANILLA)) {
            return;
        }
        final int shopId = entity.get(ZombiesEntity.SHOP_ID);
        final ArmorShop armorShop = world.getConfig().armorShops.stream()
                .filter(shop -> shop.id == shopId)
                .findAny().orElse(null);
        if (armorShop == null) {
            return;
        }
        final int gold = player.get(ZombiesPlayer.GOLD);
        if (gold < armorShop.price) {
            player.sendMessage(Component.text("You cannot afford this").color(NamedTextColor.RED));
            return;
        }
        final PlayerInventory inventory = player.getBukkit().getInventory();
        for (final EquipmentSlot equipmentSlot : armorShop.part.getEquipmentSlots()) {
            final Material armorMaterial = armorShop.quality.materials.get(equipmentSlot);
            final Material playerMaterial = inventory.getItem(equipmentSlot).getType();
            if (playerMaterial == armorMaterial) {
                player.sendMessage(Component.text("You already own this").color(NamedTextColor.RED));
                return;
            }
        }

        final int newGold = gold - armorShop.price;
        player.set(ZombiesPlayer.GOLD, newGold);
        Bukkit.getPluginManager().callEvent(new PlayerGoldChangeEvent(player, gold, newGold));
        for (final EquipmentSlot equipmentSlot : armorShop.part.getEquipmentSlots()) {
            final ItemStack item = new ItemStack(armorShop.quality.materials.get(equipmentSlot));
            final ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
            inventory.setItem(equipmentSlot, item);
        }
        player.sendMessage(Component.text("Successfully bought the armor").color(NamedTextColor.GREEN));
    }
}
