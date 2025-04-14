package io.lama06.zombies.util;

import io.lama06.zombies.CheckableConfig;
import io.lama06.zombies.InvalidConfigException;
import io.lama06.zombies.menu.*;
import io.lama06.zombies.perk.PlayerPerk;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GraphPoint implements CheckableConfig {
    public BlockPosition position;
    public String area;
    public int id;
    public List<GraphLink> links;
    public transient Location location;

    public GraphPoint() {
        links = new ArrayList<>();
        location = null;
        area = "";
    }

    public GraphPoint(final int id, final String area, final int link_id) {
        links = new ArrayList<>();
        links.add(new GraphLink(link_id));
        location = null;
        this.area = area;
        this.id = id;
    }



    public GraphPoint copy() {
        final GraphPoint point = new GraphPoint();
        point.position = position;
        point.location = location;
        point.area = area;
        point.id = id;
        point.links = new ArrayList<>();
        point.links.addAll(links);
        return point;
    }

    public void setLocation(World world) {
        location = new Location(world, position.x(), position.y(), position.z());
    }

    public String getDisplayName() {
        String s = id + " in " + (area == null ? '_' : area) + "\nconnected to";
        for (GraphLink link : links) {
            s += " " + link.id;
        }
        return s;
    }

    @Override
    public void check() throws InvalidConfigException {
        InvalidConfigException.mustBeSet(position, "position");
        InvalidConfigException.mustBeSet(area, "area");
        InvalidConfigException.mustBeSet(id, "id");
        InvalidConfigException.checkList(links, true, "links");

    }

    public void openMenu(final Player player, final Runnable callback) {
        final Runnable reopen = () -> openMenu(player, callback);
        SelectionMenu.open(
                player,
                Component.text("Tracking Point"),
                callback,
                new SelectionEntry(
                        Component.text("Position: " + PositionUtil.format(position)),
                        Material.LEVER,
                        () -> BlockPositionSelection.open(
                                player,
                                Component.text("Position"),
                                reopen,
                                position -> {
                                    this.position = position;
                                    reopen.run();
                                }
                        )
                ),
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
                ),
                new SelectionEntry(
                        Component.text("Links"),
                        Material.REDSTONE,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Links"),
                                links,
                                Material.ARROW,
                                link -> Component.text("Link to " + link.id),
                                GraphLink::new,
                                link -> link.openMenu(player, reopen),
                                reopen
                        )
                ),
                new SelectionEntry(Component.text("Area: " + area), Material.SPRUCE_DOOR, () -> InputMenu.open(
                        player,
                        Component.text("Area"),
                        area,
                        new TextInputType(),
                        area -> {
                            this.area = area;
                            openMenu(player, callback);
                        },
                        () -> openMenu(player, callback)
                ))

        );
    }


}

