package io.lama06.zombies.system;

import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.event.player.PlayerGoldChangeEvent;
import io.lama06.zombies.weapon.Weapon;
import io.lama06.zombies.weapon.WeaponType;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractWithUltimateMachine implements Listener {

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
        if (!clickedBlockPos.equals(world.getConfig().ultimateMachine.position)) {
            return;
        }

        if (!world.get(ZombiesWorld.POWER_SWITCH)) {
            player.sendMessage(Component.text("The power switch isn't enabled.").color(NamedTextColor.RED));
            return;
        }
        final Weapon heldWeapon = player.getHeldWeapon();
        if (heldWeapon == null) {
            player.sendMessage(Component.text("Select a weapon to upgrade.").color(NamedTextColor.RED));
            return;
        }
        final int gold = player.get(ZombiesPlayer.GOLD);
        if (gold < world.getConfig().ultimateMachine.gold) {
            player.sendMessage(Component.text("You don't have enough gold.").color(NamedTextColor.RED));
            return;
        }
        final WeaponType newWeapon = switch (heldWeapon.getType()) {
            case PISTOL -> WeaponType.PISTOL_ULTIMATE;
            case RIFLE -> WeaponType.RIFLE_ULTIMATE;
            case SHOTGUN -> WeaponType.SHOTGUN_ULTIMATE;
            case SNIPER -> WeaponType.SNIPER_ULTIMATE;
            case ZOMBIE_ZAPPER -> WeaponType.ZOMBIE_ZAPPER_ULTIMATE;
            case ELDER_GUN -> WeaponType.ELDER_GUN_ULTIMATE;
            case GOLD_DIGGER -> WeaponType.GOLD_DIGGER_ULTIMATE_1;
            case GOLD_DIGGER_ULTIMATE_1 -> WeaponType.GOLD_DIGGER_ULTIMATE_2;
            case GOLD_DIGGER_ULTIMATE_2 -> WeaponType.GOLD_DIGGER_ULTIMATE_3;
            case GOLD_DIGGER_ULTIMATE_3 -> WeaponType.GOLD_DIGGER_ULTIMATE_4;
            case GOLD_DIGGER_ULTIMATE_4 -> WeaponType.GOLD_DIGGER_ULTIMATE_5;
            case DOUBLE_BARREL_SHOTGUN -> WeaponType.DOUBLE_BARREL_SHOTGUN_ULTIMATE_1;
            case DOUBLE_BARREL_SHOTGUN_ULTIMATE_1 -> WeaponType.DOUBLE_BARREL_SHOTGUN_ULTIMATE_2;
            case DOUBLE_BARREL_SHOTGUN_ULTIMATE_2 -> WeaponType.DOUBLE_BARREL_SHOTGUN_ULTIMATE_3;
            default -> null;
        };
        if (newWeapon == null) {
            player.sendMessage(Component.text("This weapon can't be upgraded.").color(NamedTextColor.RED));
            return;
        }
        player.set(ZombiesPlayer.GOLD, gold - world.getConfig().ultimateMachine.gold);
        Bukkit.getPluginManager().callEvent(new PlayerGoldChangeEvent(player, gold, gold - world.getConfig().ultimateMachine.gold));

        player.giveWeapon(player.getBukkit().getInventory().getHeldItemSlot(), newWeapon);
        player.sendMessage(Component.text("Successfully upgraded the weapon.").color(NamedTextColor.GREEN));

    }
}

