package io.lama06.zombies;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.lama06.zombies.event.player.PlayerCancelCommandEvent;
import io.lama06.zombies.event.player.PlayerGoldChangeEvent;
import io.lama06.zombies.perk.PerkMachine;
import io.lama06.zombies.util.PositionUtil;
import io.lama06.zombies.weapon.WeaponType;
import io.lama06.zombies.zombie.ZombieType;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ZombiesCommandExecutor implements TabExecutor {
    private static final String DEAD_END_TEMPLATE = "templates/dead_end.json";

    @Override
    public boolean onCommand(
            final CommandSender sender,
            final Command command,
            final String label,
            final String[] args
    ) {
        if (args.length == 0) {
            root(sender);
            return true;
        }

        final String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0]) {
            case "config" -> config(sender);
            case "saveConfig" -> saveConfig(sender);
            case "checkConfig" -> checkConfig(sender);
            case "loadTemplate" -> loadTemplate(sender);
            case "start" -> start(sender);
            case "stop" -> stop(sender);
            case "giveGold" -> giveGold(sender, remainingArgs);
            case "giveWeapon" -> giveWeapon(sender, remainingArgs);
            case "spawnZombie" -> spawnZombie(sender, remainingArgs);
            case "cancel" -> cancel(sender);
            case "placeSigns" -> placeSigns(sender, remainingArgs);
            case "dumpWorldConfig" -> dumpWorldConfig(sender);
            case "placeHolograms" -> placeHolograms(sender, remainingArgs);
            case "removeHolograms" -> removeHolograms(sender);
            case "removeDisplays" -> removeDisplays(sender);
            default -> sender.sendMessage(Component.text("unknown command").color(NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
            final CommandSender sender,
            final Command command,
            final String label,
            final String[] args
    ) {
        if (args.length != 1 && args.length != 0) {
            return List.of();
        }
        return List.of(
                "config",
                "saveConfig",
                "checkConfig",
                "loadTemplate",
                "start",
                "stop",
                "giveGold",
                "giveWeapon",
                "spawnZombie",
                "placeHolograms"
        );
    }

    private void root(final CommandSender sender) {
        final Component equalSigns = Component.text("=".repeat(10)).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD);
        final Component obfuscatedChar = Component.text("_").decorate(TextDecoration.OBFUSCATED);
        final TextComponent.Builder builder = Component.text();

        builder.append(equalSigns).append(obfuscatedChar).appendSpace();
        builder.append(Component.text("Zombies-Plugin").color(NamedTextColor.LIGHT_PURPLE));
        builder.appendSpace().append(Component.text("(Version %s)".formatted(ZombiesPlugin.INSTANCE.getPluginMeta().getVersion()))
                                             .color(NamedTextColor.GREEN));
        builder.appendSpace().append(obfuscatedChar).append(equalSigns);

        builder.appendNewline();
        builder.append(Component.text("Creator: "));
        builder.append(Component.text("Lama06").color(NamedTextColor.GOLD));

        builder.appendNewline();
        builder.append(Component.text("Website: "));
        builder.append(Component.text("github.com/Lama06/Zombies-Plugin")
                               .clickEvent(ClickEvent.openUrl("https://github.com/Lama06/Zombies-Plugin/")));

        sender.sendMessage(builder);
    }

    private void config(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        if (world.isGameRunning()) {
            final String warning = "It isn't supported to edit a running game's config. " +
                    "Only continue if you know what you're doing.";
            sender.sendMessage(Component.text(warning).color(NamedTextColor.RED));
        }
        WorldConfig config = world.getConfig();
        if (config == null) {
            final ZombiesConfig globalConfig = ZombiesPlugin.INSTANCE.getGlobalConfig();
            config = new WorldConfig();
            globalConfig.worlds.put(world.getBukkit().getName(), config);
        }
        config.openMenu(player, () -> {});
    }

    private void saveConfig(final CommandSender sender) {
        try {
            ZombiesPlugin.INSTANCE.getConfigManager().saveConfig();
        } catch (final IOException e) {
            sender.sendMessage(Component.text("error: " + e.getMessage()));
            ZombiesPlugin.INSTANCE.getSLF4JLogger().error("failed to save config", e);
        }
        sender.sendMessage(Component.text("Saved").color(NamedTextColor.GREEN));
    }

    private void checkConfig(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final WorldConfig config = ZombiesPlugin.INSTANCE.getWorldConfig(new ZombiesWorld(player.getWorld()));
        if (config == null) {
            sender.sendMessage(Component.text("This world isn't configured"));
            return;
        }
        try {
            config.check();
        } catch (final InvalidConfigException e) {
            sender.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
            return;
        }
        sender.sendMessage(Component.text("No issues found").color(NamedTextColor.GREEN));
    }

    private void loadTemplate(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        final ZombiesConfig globalConfig = ZombiesPlugin.INSTANCE.getGlobalConfig();
        final InputStream resource = ZombiesPlugin.INSTANCE.getResource(DEAD_END_TEMPLATE);
        if (resource == null) {
            sender.sendMessage(Component.text("Template unavailable. Please contact the plugins's author.").color(NamedTextColor.RED));
            return;
        }
        final WorldConfig config;
        try {
            config = ConfigManager.createGson().fromJson(new InputStreamReader(resource), WorldConfig.class);
        } catch (final JsonParseException e) {
            sender.sendMessage(Component.text("Template malformed. Please contact the plugins's author."));
            sender.sendMessage(Component.text(e.getMessage()));
            return;
        }
        globalConfig.worlds.put(world.getBukkit().getName(), config);
        sender.sendMessage(Component.text("Template loaded. Start the game: Click me!").color(NamedTextColor.GREEN)
                                   .clickEvent(ClickEvent.suggestCommand("/zombies start")));
    }

    private void start(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        if (!world.isZombiesWorld()) {
            final Component msg = Component.text("You must first configure this world.").color(NamedTextColor.RED)
                    .appendNewline()
                    .append(Component.text("> Configure manually <")
                                    .clickEvent(ClickEvent.runCommand("/zombies config"))
                                    .color(NamedTextColor.BLUE))
                    .appendNewline()
                    .append(Component.text("> Load Dead End config (recommended) <")
                                    .clickEvent(ClickEvent.runCommand("/zombies loadTemplate"))
                                    .color(NamedTextColor.GREEN));
            sender.sendMessage(msg);
            return;
        }
        if (world.isGameRunning()) {
            sender.sendMessage(Component.text("The game is already running").color(NamedTextColor.RED));
            return;
        }
        try {
            world.getConfig().check();
        } catch (final InvalidConfigException e) {
            player.sendMessage(Component.text("The config is invalid: " + e.getMessage()).color(NamedTextColor.RED));
            return;
        }
        world.startGame();
    }



    private void stop(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        if (!world.isZombiesWorld() || !world.isGameRunning()) {
            sender.sendMessage(Component.text("The game isn't running").color(NamedTextColor.RED));
            return;
        }
        world.endGame();
    }

    private void giveWeapon(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            return;
        }
        final WeaponType weaponType;
        try {
            weaponType = WeaponType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException e) {
            return;
        }
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesPlayer zombiesPlayer = new ZombiesPlayer(player);
        zombiesPlayer.giveWeapon(player.getInventory().getHeldItemSlot(), weaponType);
    }

    private void giveGold(final CommandSender sender, final String[] args) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesPlayer zombiesPlayer = new ZombiesPlayer(player);
        if (args.length == 0) {
            return;
        }
        final int goldAdd;
        try {
            goldAdd = Integer.parseInt(args[0]);
        } catch (final NumberFormatException e) {
            return;
        }
        final int goldPrevious = zombiesPlayer.get(ZombiesPlayer.GOLD);
        final int golfAfter = goldPrevious + goldAdd;
        zombiesPlayer.set(ZombiesPlayer.GOLD, golfAfter);
        Bukkit.getPluginManager().callEvent(new PlayerGoldChangeEvent(zombiesPlayer, goldPrevious, golfAfter));
    }

    private void spawnZombie(final CommandSender sender, final String[] args) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        if (!world.isGameRunning()) {
            return;
        }
        if (args.length == 0) {
            return;
        }
        final ZombieType zombieType;
        try {
            zombieType = ZombieType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException e) {
            return;
        }
        world.spawnZombie(player.getLocation(), zombieType);
    }

    private void cancel(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new PlayerCancelCommandEvent(player));
    }

    private record SignPosition(Block block, BlockFace direction) { }

    @FunctionalInterface
    private interface SignPositionFetcher {
        Optional<SignPosition> getSignPosition(final World world, final BlockPosition position);
    }

    private Optional<SignPosition> getShopSignPosition(final World world, final BlockPosition position) {
        final Block signBlock = position.toLocation(world).getBlock();
        Block neighbour = null;
        int modX, modZ = 0;
        searchNeighbour:
        for (modX = -1; modX <= 1; modX++) {
            for (modZ = -1 ; modZ <= 1; modZ++) {
                if ((modX == 0) == (modZ == 0)) {
                    continue;
                }
                final Block neighbourCandidate = signBlock.getRelative(modX, 0, modZ);
                if (neighbourCandidate.getType().isEmpty()) {
                    continue;
                }
                neighbour = neighbourCandidate;
                break searchNeighbour;
            }
        }
        if (neighbour == null) {
            return Optional.empty();
        }
        final int finalModX = modX;
        final int finalModZ = modZ;
        final BlockFace directionToNeighbour = Arrays.stream(BlockFace.values())
                .filter(face -> face.getModX() == finalModX && face.getModZ() == finalModZ)
                .findAny().orElseThrow();
        final BlockFace signDirection = directionToNeighbour.getOppositeFace();
        return Optional.of(new SignPosition(signBlock, signDirection));
    }

    private Optional<SignPosition> getPerkSignPosition(final World world, final BlockPosition position) {
        final Block buttonBlock = position.toLocation(world).getBlock();
        if (!(buttonBlock.getBlockData() instanceof final Switch buttonData)) {
            return Optional.empty();
        }
        final BlockFace signDirection = buttonData.getFacing();
        final Block signBlock = buttonBlock.getRelative(BlockFace.UP);
        return Optional.of(new SignPosition(signBlock, signDirection));
    }

    private boolean placeSign(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position,
            final List<? extends Component> lines
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return false;
        }
        final Block signBlock = signPosition.get().block();
        signBlock.setType(Material.OAK_WALL_SIGN);
        final WallSign signData = (WallSign) signBlock.getBlockData();
        signData.setFacing(signPosition.get().direction());
        signBlock.setBlockData(signData);
        final Sign signState = (Sign) signBlock.getState();
        final SignSide signFront = signState.getSide(Side.FRONT);
        signFront.setGlowingText(true);
        for (int i = 0; i < lines.size(); i++) {
            final Component line = lines.get(i);
            signFront.line(i, line);
        }
        signState.update();
        return true;
    }


    private boolean placeButton(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return false;
        }
        final Block button = signPosition.get().block();
        button.setType(Material.STONE_BUTTON);

        return true;
    }


    private boolean placeHologram(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position,
            final Component component
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return false;
        }
        final TextDisplay hologram = (TextDisplay) world.spawnEntity(
                signPosition.get().block.getLocation().toCenterLocation().add(0, 0.5, 0).setDirection(signPosition.get().direction.getDirection()),
                EntityType.TEXT_DISPLAY
        );
        hologram.text(component);
        hologram.setRotation(signPosition.get().direction.getDirection().angle(new Vector(1,0,0)), 0);

        final ZombiesEntity entity = new ZombiesEntity(hologram);
        entity.set(ZombiesEntity.IS_NOT_VANILLA, true);

        return true;


    }

    private boolean placeItem(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position,
            final Material material
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return false;
        }

        final ItemDisplay item = (ItemDisplay) world.spawnEntity(
                signPosition.get().block.getLocation().toCenterLocation().add(0, -1.5, 0).setDirection(signPosition.get().direction.getDirection()),
                EntityType.ITEM_DISPLAY
        );

        // item.setRotation((float) (signPosition.get().direction.getDirection().angle(new Vector(-1, 0, 0)) * 180.0 / 3.14), 0);


        item.setItemStack(new ItemStack(material));
        item.setNoPhysics(true);
        item.setInvulnerable(true);

        final ZombiesEntity entity = new ZombiesEntity(item);
        entity.set(ZombiesEntity.IS_NOT_VANILLA, true);

        return true;
    }

    private ArmorStand placeArmorStand(
            final SignPositionFetcher fetcher,
            final World world,
            final BlockPosition position
    ) {
        final Optional<SignPosition> signPosition = fetcher.getSignPosition(world, position);
        if (signPosition.isEmpty()) {
            return null;
        }

        final ArmorStand stand = (ArmorStand) world.spawnEntity(
                signPosition.get().block.getLocation().toCenterLocation().add(0, -1.5, 0).setDirection(signPosition.get().direction.getDirection()),
                EntityType.ARMOR_STAND
        );

        stand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        stand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        stand.addEquipmentLock(EquipmentSlot.BODY, ArmorStand.LockType.ADDING_OR_CHANGING);
        stand.setCanTick(false);
        stand.setInvulnerable(true);
        stand.setInvisible(true);
        stand.setNoPhysics(true);
        stand.setGravity(false);

        return stand;
    }


    private void removeHolograms(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }

        for (final Entity entity : player.getWorld().getEntities()) {
            final ZombiesEntity zombiesEntity = new ZombiesEntity(entity);
            if (zombiesEntity.isNotVanilla()) {
                entity.remove();
            }
        }

    }

    private void removeDisplays(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }

        for (final Entity entity : player.getWorld().getEntities()) {
            if (entity.getType() == EntityType.TEXT_DISPLAY || entity.getType() == EntityType.ITEM_DISPLAY) {
                entity.remove();
            }
        }

    }

    private void placeHolograms(final CommandSender sender, final String[] args) {
        if (!(sender instanceof final Player player)) {
            return;
        }

        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        if (!world.isZombiesWorld()) {
            return;
        }
        final WorldConfig config = world.getConfig();
        final List<BlockPosition> errors = new ArrayList<>();


        for (final ArmorShop armorShop : config.armorShops) {
            if (armorShop.position == null || armorShop.quality == null || armorShop.part == null) {
                continue;
            }
            final boolean ok = placeHologram(this::getShopSignPosition, world.getBukkit(), armorShop.position,
                    armorShop.quality.getDisplayName().append(Component.text(" Armor")).appendNewline()
                            .append(armorShop.part.getDisplayName()).appendNewline()
                            .append(Component.text(armorShop.price + " Gold").color(NamedTextColor.GOLD))
            );


            final ArmorStand stand = placeArmorStand(this::getShopSignPosition, world.getBukkit(), armorShop.position);
            if (stand != null) {
                for (final EquipmentSlot equipmentSlot : armorShop.part.getEquipmentSlots()) {
                    final ItemStack item = new ItemStack(armorShop.quality.materials.get(equipmentSlot));
                    stand.setItem(equipmentSlot, item);
                }
            }

            final ZombiesEntity entity = new ZombiesEntity(stand);
            entity.set(ZombiesEntity.IS_NOT_VANILLA, true);
            entity.set(ZombiesEntity.SHOP_ID, armorShop.id);


            if (!ok) {
                errors.add(armorShop.position);
            }
        }


        for (final WeaponShop weaponShop : config.weaponShops) {
            if (weaponShop.position == null || weaponShop.weaponType == null) {
                continue;
            }
            final boolean ok = placeHologram(this::getShopSignPosition, world.getBukkit(), weaponShop.position,
                    weaponShop.weaponType.getDisplayName().appendNewline()
                            .append(Component.text(weaponShop.purchasePrice + " Gold").color(NamedTextColor.GOLD)).appendNewline()
                            .append(Component.text("Right Click to purchase"))
            ) && placeItem(this::getShopSignPosition, world.getBukkit(), weaponShop.position, weaponShop.weaponType.data.material);

            final ArmorStand stand = placeArmorStand(this::getShopSignPosition, world.getBukkit(), weaponShop.position);
            final ZombiesEntity entity = new ZombiesEntity(stand);
            entity.set(ZombiesEntity.IS_NOT_VANILLA, true);
            entity.set(ZombiesEntity.SHOP_ID, weaponShop.id);

            if (!ok) {
                errors.add(weaponShop.position);
            }
        }

        for (final PerkMachine perkMachine : config.perkMachines) {
            if (perkMachine.position == null || perkMachine.perk == null) {
                continue;
            }
            final boolean ok = placeHologram(this::getPerkSignPosition, world.getBukkit(), perkMachine.position,
                    perkMachine.perk.getDisplayName()
                            .append(Component.text(" " + perkMachine.gold + " Gold").color(NamedTextColor.GOLD))
            );
            if (!ok) {
                errors.add(perkMachine.position);
            }
        }


        for (final BlockPosition error : errors) {
            sender.sendMessage(Component.text("Failed to place sign at " + PositionUtil.format(error)).color(NamedTextColor.RED));
        }
        sender.sendMessage(Component.text("Done").color(NamedTextColor.GREEN));
    }


    private void placeSigns(final CommandSender sender, final String[] args) {
        if (!(sender instanceof final Player player)) {
            return;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("ok")) {
            final TextComponent.Builder builder = Component.text();
            builder.append(Component.text("Executing this command will place signs at every armor and weapon shop."));
            builder.appendNewline();
            builder.append(Component.text("Existing blocks will be removed. This cannot be reverted").color(NamedTextColor.RED));
            builder.appendNewline();
            builder.append(Component.text("> Click here to confirm <")
                                   .clickEvent(ClickEvent.runCommand("/zombies placeSigns ok"))
                                   .color(NamedTextColor.BLUE));
            sender.sendMessage(builder);
            return;
        }

        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        if (!world.isZombiesWorld()) {
            return;
        }
        final WorldConfig config = world.getConfig();
        final List<BlockPosition> errors = new ArrayList<>();
        for (final ArmorShop armorShop : config.armorShops) {
            if (armorShop.position == null || armorShop.quality == null || armorShop.part == null) {
                continue;
            }
            final boolean ok = placeSign(this::getShopSignPosition, world.getBukkit(), armorShop.position, List.of(
                    armorShop.quality.getDisplayName().append(Component.text(" Armor")),
                    armorShop.part.getDisplayName(),
                    Component.text(armorShop.price + " Gold").color(NamedTextColor.GOLD)
            ));
            if (!ok) {
                errors.add(armorShop.position);
            }
        }
        for (final WeaponShop weaponShop : config.weaponShops) {
            if (weaponShop.position == null || weaponShop.weaponType == null) {
                continue;
            }
            final boolean ok = placeSign(this::getShopSignPosition, world.getBukkit(), weaponShop.position, List.of(
                    weaponShop.weaponType.getDisplayName(),
                    Component.text(weaponShop.purchasePrice + " Gold").color(NamedTextColor.GOLD),
                    Component.text("Refill: ").append(Component.text(weaponShop.refillPrice + " Gold").color(NamedTextColor.GOLD))
            ));
            if (!ok) {
                errors.add(weaponShop.position);
            }
        }
        for (final PerkMachine perkMachine : config.perkMachines) {
            if (perkMachine.position == null || perkMachine.perk == null) {
                continue;
            }
            final boolean ok = placeSign(this::getPerkSignPosition, world.getBukkit(), perkMachine.position, List.of(
                    perkMachine.perk.getDisplayName(),
                    Component.text(perkMachine.gold + " Gold").color(NamedTextColor.GOLD)
            ));
            if (!ok) {
                errors.add(perkMachine.position);
            }
        }
        for (final BlockPosition error : errors) {
            sender.sendMessage(Component.text("Failed to place sign at " + PositionUtil.format(error)).color(NamedTextColor.RED));
        }
        sender.sendMessage(Component.text("Done").color(NamedTextColor.GREEN));
    }

    private void dumpWorldConfig(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }
        final ZombiesWorld world = new ZombiesWorld(player.getWorld());
        if (!world.isZombiesWorld()) {
            return;
        }
        final WorldConfig config = world.getConfig();
        final Gson gson = ConfigManager.createGson();
        final String json = gson.toJson(config);
        sender.sendMessage(Component.text("> Copy <").clickEvent(ClickEvent.copyToClipboard(json)).color(NamedTextColor.GREEN));
    }
}
