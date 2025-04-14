package io.lama06.zombies.util;

import io.lama06.zombies.CheckableConfig;
import io.lama06.zombies.InvalidConfigException;
import io.lama06.zombies.menu.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GraphDoorLink implements CheckableConfig {
    public int first_id;
    public int second_id;
    public double distance;

    public GraphDoorLink() {}

    @Override
    public void check() throws InvalidConfigException {
        InvalidConfigException.mustBeSet(first_id, "first_id");
        InvalidConfigException.mustBeSet(second_id, "second_id");
        InvalidConfigException.mustBeSet(distance, "distance");

    }

    public void openMenu(final Player player, final Runnable callback) {
        final Runnable reopen = () -> openMenu(player, callback);
        SelectionMenu.open(
                player,
                Component.text("Tracking point link"),
                callback,
                new SelectionEntry(
                        Component.text("First id: " + first_id),
                        Material.CYAN_DYE,
                        () -> InputMenu.open(
                                player,
                                Component.text("First id"),
                                first_id,
                                new IntegerInputType(),
                                id -> {
                                    first_id = id;
                                    reopen.run();
                                },
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Second id: " + second_id),
                        Material.ORANGE_DYE,
                        () -> InputMenu.open(
                                player,
                                Component.text("Second id"),
                                second_id,
                                new IntegerInputType(),
                                id -> {
                                    second_id = id;
                                    reopen.run();
                                },
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Distance: " + distance),
                        Material.REPEATER,
                        () -> InputMenu.open(
                                player,
                                Component.text("Distance"),
                                distance,
                                new DoubleInputType(),
                                dist -> {
                                    distance = dist;
                                    reopen.run();
                                },
                                reopen
                        )
                )
        );
    }
}


