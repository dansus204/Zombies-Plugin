package io.lama06.zombies.system.weapon.attack;

import io.lama06.zombies.perk.GlobalPerk;
import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.event.player.PlayerAttackZombieEvent;
import io.lama06.zombies.event.player.PlayerGoldChangeEvent;
import io.lama06.zombies.ZombiesPlayer;
import io.lama06.zombies.weapon.AttackData;
import io.lama06.zombies.weapon.Weapon;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class GiveGoldAfterAttackSystem implements Listener {
    @EventHandler
    private void onPlayerAttacksZombie(final PlayerAttackZombieEvent event) {
        final Weapon weapon = event.getWeapon();
        final AttackData attackData = weapon.getData().attack;
        if (attackData == null) {
            return;
        }
        final ZombiesPlayer player = event.getPlayer();
        final ZombiesWorld world = event.getWorld();
        final int goldBefore = player.get(ZombiesPlayer.GOLD);

        int goldAdd = attackData.gold();
        String comment = "";
        if (event.isCritical()) {
            goldAdd = attackData.crit_gold();
            comment = " (Critical!)";
        }
        if (world.isPerkEnabled(GlobalPerk.INSTANT_KILL)) {
            goldAdd = 50;
        }
        if (world.isPerkEnabled(GlobalPerk.DOUBLE_GOLD)) {
            goldAdd *= 2;
        }


        final int goldAfter = goldBefore + goldAdd;
        player.set(ZombiesPlayer.GOLD, goldAfter);

        player.sendMessage(Component.text((
                "+%s Gold" + comment
                                          ).formatted(goldAdd)).color(NamedTextColor.GOLD));
        player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.BLOCK, 1, event.isCritical() ? 1.5F : 2F));

        Bukkit.getPluginManager().callEvent(new PlayerGoldChangeEvent(player, goldBefore, goldAfter));
    }
}
