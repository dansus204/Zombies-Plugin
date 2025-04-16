package io.lama06.zombies.util;

import io.lama06.zombies.ZombiesEntity;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.List;

public class HologramUtil {

    public static List<TextDisplay> placeHologram(
            final World world,
            final FinePosition position,
            final List<Component> components
    ) {

        final List<TextDisplay> displays = new ArrayList<>();
        int i = 0;
        for (final Component component : components) {
            final TextDisplay hologram = (TextDisplay) world.spawnEntity(
                    position.toLocation(world).add(0, - 0.25 * i++ + 0.25 * components.size(), 0),
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
}
