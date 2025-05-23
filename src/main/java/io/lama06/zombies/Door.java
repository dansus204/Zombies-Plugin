package io.lama06.zombies;

import io.lama06.zombies.menu.*;
import io.lama06.zombies.util.BlockArea;
import io.lama06.zombies.util.GraphDoorLink;
import io.lama06.zombies.util.PositionUtil;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.List;

public final class Door implements CheckableConfig {
    public String area1 = "";
    public String area2 = "";
    public BlockPosition pos1;
    public BlockPosition pos2;
    public int gold = 750;
    public BlockArea position;
    public BlockArea templateOpen;
    public BlockArea templateClosed;
    public List<GraphDoorLink> links = new ArrayList<>();
    public transient List<TextDisplay> hologram1 = new ArrayList<>();
    public transient List<TextDisplay> hologram2 = new ArrayList<>();

    public void setOpen(final ZombiesWorld world, final boolean open) {
        if (open && templateOpen == null) {
            position.fill(world.getBukkit(), Material.AIR.createBlockData());
            return;
        }
        (open ? templateOpen : templateClosed).clone(world.getBukkit(), position);


    }



    @Override
    public void check() throws InvalidConfigException {
        InvalidConfigException.mustBeSet(area1, "first area");
        InvalidConfigException.mustBeSet(area2, "second area");
        InvalidConfigException.mustBeSet(pos1, "pos1");
        InvalidConfigException.mustBeSet(pos2, "pos2");

        InvalidConfigException.mustBeSet(position, "position");
        InvalidConfigException.mustBeSet(templateClosed, "template closed");
        InvalidConfigException.checkList(links, true, "links");

        if (area1.equals(area2)) {
            throw new InvalidConfigException("the first area equals the second area");
        }
        if (!position.hasSameDimensions(templateClosed)) {
            throw new InvalidConfigException("closed template doesn't have the same dimensions as the door");
        }
        if (templateOpen != null && !position.hasSameDimensions(templateOpen)) {
            throw new InvalidConfigException("open template doesn't have the same dimensions as the door");
        }
    }

    public void openMenu(final Player player, final Runnable callback) {
        final Runnable reopen = () -> openMenu(player, callback);
        SelectionMenu.open(
                player,
                Component.text("Edit Door"),
                callback,
                new SelectionEntry(Component.text("First Area: " + area1), Material.ACACIA_DOOR, () -> InputMenu.open(
                        player,
                        Component.text("First Area"),
                        area1,
                        new TextInputType(),
                        area1 -> {
                            this.area1 = area1;
                            reopen.run();
                        },
                        reopen
                )),
                new SelectionEntry(Component.text("Second Area: " + area2), Material.SPRUCE_DOOR, () -> InputMenu.open(
                        player,
                        Component.text("Second Area"),
                        area2,
                        new TextInputType(),
                        area2 -> {
                            this.area2 = area2;
                            openMenu(player, callback);
                        },
                        () -> openMenu(player, callback)
                )),
                new SelectionEntry(
                        Component.text("Hologram position 1: " + PositionUtil.format(pos1)),
                        Material.END_ROD,
                        () -> BlockPositionSelection.open(
                                player,
                                Component.text("Position"),
                                reopen,
                                pos -> {
                                    pos1 = pos;
                                    reopen.run();
                                }
                        )
                ),
                new SelectionEntry(
                        Component.text("Hologram position 1: " + PositionUtil.format(pos2)),
                        Material.END_ROD,
                        () -> BlockPositionSelection.open(
                                player,
                                Component.text("Position"),
                                reopen,
                                pos -> {
                                    pos2 = pos;
                                    reopen.run();
                                }
                        )
                ),
                new SelectionEntry(Component.text("Gold: " + gold), Material.GOLD_NUGGET, () -> InputMenu.open(
                        player,
                        Component.text("Gold").color(NamedTextColor.GOLD),
                        gold,
                        new IntegerInputType(),
                        gold -> {
                            this.gold = gold;
                            openMenu(player, callback);
                        },
                        () -> openMenu(player, callback)
                )),
                new SelectionEntry(Component.text("Position: " + position), Material.CHERRY_DOOR, () -> BlockAreaSelection.open(
                        player,
                        Component.text("Door Position"),
                        callback,
                        position -> {
                            this.position = position;
                            openMenu(player, callback);
                        }
                )),
                new SelectionEntry(
                        Component.text("Template Open: " + templateOpen),
                        Material.STRUCTURE_BLOCK,
                        () -> BlockAreaSelection.open(
                                player,
                                Component.text("Template Open"),
                                callback,
                                templateOpen -> {
                                    this.templateOpen = templateOpen;
                                    openMenu(player, callback);
                                }
                        ),
                        Component.text("Reset"),
                        () -> {
                            templateOpen = null;
                            openMenu(player, callback);
                        }
                ),
                new SelectionEntry(
                        Component.text("Template Closed: " + templateClosed),
                        Material.STRUCTURE_BLOCK,
                        () -> BlockAreaSelection.open(
                                player,
                                Component.text("Template Closed"),
                                callback,
                                templateClosed -> {
                                    this.templateClosed = templateClosed;
                                    openMenu(player, callback);
                                }

                        )
                ),
                new SelectionEntry(
                        Component.text("Tracking point links"),
                        Material.ARROW,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Tracking point links"),
                                links,
                                Material.ARROW,
                                link -> Component.text("Link from " + link.first_id +
                                                               " to " + link.second_id + ". Distance: " + link.distance),
                                GraphDoorLink::new,
                                link -> link.openMenu(player, reopen),
                                reopen
                        )
                )
        );
    }
}
