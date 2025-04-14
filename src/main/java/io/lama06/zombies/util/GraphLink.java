package io.lama06.zombies.util;

import io.lama06.zombies.CheckableConfig;
import io.lama06.zombies.InvalidConfigException;
import io.lama06.zombies.menu.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GraphLink implements CheckableConfig {
    public int id;

    public GraphLink() {}

    public GraphLink(final int id) {
        this.id = id;
    }

    @Override
    public void check() throws InvalidConfigException {
        InvalidConfigException.mustBeSet(id, "id");

    }

    public void openMenu(final Player player, final Runnable callback) {
    final Runnable reopen = () -> openMenu(player, callback);
    SelectionMenu.open(
            player,
            Component.text("Link"),
            callback,
            new SelectionEntry(
                    Component.text("Id: " + id).color(NamedTextColor.GREEN),
                    Material.GREEN_DYE,
                    () -> InputMenu.open(
                            player,
                            Component.text("Id").color(NamedTextColor.GREEN),
                            id,
                            new IntegerInputType(),
                            id -> {
                                this.id = id;
                                reopen.run();
                            },
                            reopen
                    )
            )
        );
    }
}


