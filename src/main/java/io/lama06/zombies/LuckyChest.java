package io.lama06.zombies;

import io.lama06.zombies.menu.*;
import io.lama06.zombies.system.PrepareWorldAtGameStartSystem;
import io.lama06.zombies.util.PositionUtil;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LuckyChest implements CheckableConfig {
    public BlockPosition position;
    public String area = "";
    public int gold = 1000;
    public transient int uses_left = 0;
    public transient int state = 0;
    /*
    0 - inactive
    1 - rolling
    2 - waiting
     */

    public transient TextDisplay timer;
    public transient ItemDisplay display;
    public transient List<TextDisplay> textDisplays = null;

    @Override
    public void check() throws InvalidConfigException {
        InvalidConfigException.mustBeSet(position, "position");
        InvalidConfigException.mustBeSet(area, "area");
    }

    public Block getSecondChestBlock(final World world) {
        final Block block = position.toLocation(world).getBlock();
        final BlockData blockData = block.getBlockData();
        if (!(blockData instanceof final Chest chest)) {
            return null;
        }
        final Block secondChestBlock = switch (chest.getType()) {
            case SINGLE -> null;
            case RIGHT -> switch (chest.getFacing()) {
                case NORTH -> block.getRelative(BlockFace.WEST);
                case WEST -> block.getRelative(BlockFace.SOUTH);
                case SOUTH -> block.getRelative(BlockFace.EAST);
                case EAST -> block.getRelative(BlockFace.NORTH);
                default -> null;
            };
            case LEFT -> switch (chest.getFacing()) {
                case NORTH -> block.getRelative(BlockFace.EAST);
                case EAST -> block.getRelative(BlockFace.SOUTH);
                case SOUTH -> block.getRelative(BlockFace.WEST);
                case WEST -> block.getRelative(BlockFace.NORTH);
                default -> null;
            };
        };
        if (secondChestBlock == null) {
            return null;
        }
        if (secondChestBlock.getType() != Material.CHEST) {
            return null;
        }
        return secondChestBlock;
    }

    public FinePosition getItemPosition(final World world) {
        final Block secondChestBlock = getSecondChestBlock(world);
        if (secondChestBlock == null) {
            return position.toCenter().offset(0, 1, 0);
        }
        return PositionUtil.getMidpoint(position.toCenter(), secondChestBlock.getLocation().toCenter()).offset(0, 1, 0);
    }

    public List<TextDisplay> placeHologram(
            final World world,
            final List<Component> components
    ) {

        final List<TextDisplay> displays = new ArrayList<>();
        int i = 0;
        for (final Component component : components) {
            final TextDisplay hologram = (TextDisplay) world.spawnEntity(
                    getItemPosition(world).toLocation(world).add(0, 0.5 - 0.25 * i++ + 0.25 * components.size(), 0),
                    EntityType.TEXT_DISPLAY
            );
            hologram.text(component);
            hologram.setBillboard(Display.Billboard.CENTER);


            final ZombiesEntity entity = new ZombiesEntity(hologram);
            entity.set(ZombiesEntity.IS_NOT_VANILLA, true);
            displays.add(hologram);
        }

        return displays;
    }


    public void openMenu(final Player player, final Runnable callback) {
        final Runnable reopen = () -> openMenu(player, callback);
        SelectionMenu.open(
                player,
                Component.text("Lucky Chest"),
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
                        Component.text("Area: " + area),
                        Material.ACACIA_DOOR,
                        () -> InputMenu.open(
                            player,
                            Component.text("Area"),
                            area,
                            new TextInputType(),
                            area1 -> {
                                this.area = area1;
                                reopen.run();
                            },
                            reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Gold: " + gold),
                        Material.GOLD_NUGGET,
                        () -> InputMenu.open(
                                player,
                                Component.text("Lucky Chest Price"),
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
