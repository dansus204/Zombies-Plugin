package io.lama06.zombies;

import io.lama06.zombies.menu.*;
import io.lama06.zombies.util.PositionUtil;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class UltimateMachine implements CheckableConfig {
    public BlockPosition position;
    public int gold = 1500;


    @Override
    public void check() throws InvalidConfigException {
        InvalidConfigException.mustBeSet(position, "position");
    }

    public void openMenu(final Player player, final Runnable callback) {
        final Runnable reopen = () -> openMenu(player, callback);
        SelectionMenu.open(
                player,
                Component.text("Ultimate Machine"),
                callback,
                new SelectionEntry(
                        Component.text("Position: " + PositionUtil.format(position)),
                        Material.CHEST,
                        () -> BlockPositionSelection.open(
                                player,
                                Component.text("Lucky Chest Position"),
                                reopen,
                                position -> {
                                    this.position = position;
                                    reopen.run();
                                }
                        )
                ),
                new SelectionEntry(
                        Component.text("Gold: " + gold),
                        Material.GOLD_NUGGET,
                        () -> InputMenu.open(
                                player,
                                Component.text("Ultimate machine Price"),
                                gold,
                                new IntegerInputType(),
                                gold -> {
                                    this.gold = gold;
                                    reopen.run();
                                },
                                reopen
                        )
                )
        );
    }
}
