package io.lama06.zombies;

import io.lama06.zombies.menu.*;
import io.lama06.zombies.perk.PerkMachine;
import io.lama06.zombies.util.Graph;
import io.lama06.zombies.util.GraphPoint;
import io.lama06.zombies.util.PositionUtil;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class WorldConfig implements CheckableConfig {
    public String startArea = "";
    public final List<Door> doors = new ArrayList<>();
    public final List<Window> windows = new ArrayList<>();
    public final List<WeaponShop> weaponShops = new ArrayList<>();
    public final List<ArmorShop> armorShops = new ArrayList<>();
    public final List<LuckyChest> luckyChests = new ArrayList<>();
    public final List<PerkMachine> perkMachines = new ArrayList<>();
    public final List<GraphPoint> graphPoints = new ArrayList<>();
    public PowerSwitch powerSwitch;
    public UltimateMachine ultimateMachine;
    public BlockPosition teamMachine;
    public boolean autoStartStop;
    public boolean preventBuilding;
    public transient Graph graph;

    public LuckyChest setNewLuckyChest(final LuckyChest exclude) {
        for (final LuckyChest luckyChest : luckyChests) {
            luckyChest.uses_left = 0;
        }
        final RandomGenerator rnd = ThreadLocalRandom.current();
        int chestIndex = rnd.nextInt(luckyChests.size());
        while (luckyChests.get(chestIndex) == exclude) {
            chestIndex = rnd.nextInt(luckyChests.size());
        }

        luckyChests.get(chestIndex).uses_left = rnd.nextInt(10, 15);
        return luckyChests.get(chestIndex);
    }

    @Override
    public void check() throws InvalidConfigException {
        InvalidConfigException.mustBeSet(startArea, "start area");
        InvalidConfigException.checkList(doors, true, "doors");
        InvalidConfigException.checkList(windows, false, "windows");
        InvalidConfigException.checkList(weaponShops, true, "weapon shops");
        InvalidConfigException.checkList(armorShops, true, "armor shops");
        InvalidConfigException.checkList(luckyChests, true, "lucky chests");
        InvalidConfigException.checkList(perkMachines, true, "perk machines");
        InvalidConfigException.checkList(perkMachines, true, "tracking points");
        if (powerSwitch != null) {
            powerSwitch.check();
        }
    }

    public void openMenu(final Player player, final Runnable callback) {
        final Runnable reopen = () -> openMenu(player, callback);

        SelectionMenu.open(
                player,
                Component.text("Zombies Configuration").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                callback,
                new SelectionEntry(
                        Component.text("Start Area: " + (startArea.isEmpty() ? "_" : startArea)),
                        Material.OAK_FENCE,
                        () -> InputMenu.open(
                                player,
                                Component.text("Start Area"),
                                startArea,
                                new TextInputType(),
                                startArea -> {
                                    this.startArea = startArea;
                                    reopen.run();
                                },
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Doors"),
                        Material.ACACIA_DOOR,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Doors"),
                                doors,
                                Material.ACACIA_DOOR,
                                door -> Component.text("Door from %s to %s".formatted(door.area1.isEmpty() ? "_" : door.area1, door.area2.isEmpty() ? "_" : door.area2)),
                                Door::new,
                                door -> door.openMenu(player, reopen),
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Windows"),
                        Material.GLASS,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Windows"),
                                windows,
                                Material.ACACIA_DOOR,
                                window -> Component.text("Window in: " + (window.area.isEmpty() ? "_" : window.area) + ". Position: " + window.repairArea + " Point " + window.closestPointId),
                                Window::new,
                                window -> window.openMenu(player, reopen),
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Weapon Shops"),
                        Material.WOODEN_HOE,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Weapon Shops"),
                                weaponShops,
                                Material.WOODEN_HOE,
                                shop -> Component.text("Weapon Shop: ").append(shop.weaponType.getDisplayName()),
                                WeaponShop::new,
                                shop -> shop.openMenu(player, reopen),
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Armor Shops"),
                        Material.IRON_CHESTPLATE,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Armor Shops"),
                                armorShops,
                                Material.IRON_CHESTPLATE,
                                shop -> Component.text("Armor Shop: ").append(shop.quality.getDisplayName())
                                        .appendSpace().append(shop.part.getDisplayName()),
                                ArmorShop::new,
                                shop -> shop.openMenu(player, reopen),
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Lucky Chests"),
                        Material.CHEST,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Lucky Chests"),
                                luckyChests,
                                Material.CHEST,
                                luckyChest -> Component.text("Lucky Chest at " + PositionUtil.format(luckyChest.position) + " in " + luckyChest.area),
                                LuckyChest::new,
                                luckChest -> luckChest.openMenu(player, reopen),
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Perk Machines"),
                        Material.COMMAND_BLOCK,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Perk Machines"),
                                perkMachines,
                                Material.COMMAND_BLOCK,
                                machine -> Component.text("Perk Machine: ").append(machine.perk.getDisplayName()),
                                PerkMachine::new,
                                machine -> machine.openMenu(player, reopen),
                                reopen
                        )
                ),
                new SelectionEntry(
                        Component.text("Tracking Points"),
                        Material.ARROW,
                        () -> ListConfigMenu.open(
                                player,
                                Component.text("Tracking Points"),
                                graphPoints,
                                Material.ARROW,
                                point -> Component.text("Point: " + point.getDisplayName()),
                                () -> new GraphPoint(graphPoints.getLast().id + 1, graphPoints.getLast().area, graphPoints.getLast().id),
                                point -> point.openMenu(player, reopen),
                                reopen
                        )
                ),

                new SelectionEntry(
                        Component.text("Power Switch" + (powerSwitch == null ? ": null" : "")),
                        Material.LEVER,
                        () -> (powerSwitch != null ? powerSwitch : (powerSwitch = new PowerSwitch())).openMenu(player, reopen),
                        Component.text("Remove").color(NamedTextColor.RED),
                        () -> {
                            powerSwitch = null;
                            reopen.run();
                        }
                ),
                new SelectionEntry(
                        Component.text("Ultimate Machine" + (ultimateMachine == null ? ": null" : "")),
                        Material.LEVER,
                        () -> (ultimateMachine != null ? ultimateMachine : (ultimateMachine = new UltimateMachine())).openMenu(player, reopen),
                        Component.text("Remove").color(NamedTextColor.RED),
                        () -> {
                            ultimateMachine = null;
                            reopen.run();
                        }
                ),
                new SelectionEntry(
                        Component.text("Team Machine: " + PositionUtil.format(teamMachine)),
                        Material.IRON_BLOCK,
                        () -> BlockPositionSelection.open(
                                player,
                                Component.text("Team Machine Position"),
                                reopen,
                                teamMachine -> {
                                    this.teamMachine = teamMachine;
                                    reopen.run();
                                }
                        ),
                        Component.text("Remove").color(NamedTextColor.RED),
                        () -> {
                            teamMachine = null;
                            reopen.run();
                        }
                ),
                new SelectionEntry(
                        Component.text("Auto Start / Stop: " + autoStartStop),
                        Material.CLOCK,
                        () -> {
                            autoStartStop = !autoStartStop;
                            reopen.run();
                        }
                ),
                new SelectionEntry(
                        Component.text("Prevent Building by Operators: " + preventBuilding),
                        Material.BARRIER,
                        () -> {
                            preventBuilding = !preventBuilding;
                            reopen.run();
                        }
                )
        );
    }
}
