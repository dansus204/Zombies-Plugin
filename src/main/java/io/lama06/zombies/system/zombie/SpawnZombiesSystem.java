package io.lama06.zombies.system.zombie;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.lama06.zombies.*;
import io.lama06.zombies.zombie.Zombie;
import io.lama06.zombies.zombie.ZombieType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

public final class SpawnZombiesSystem implements Listener {
    @EventHandler
    private void onServerTick(final ServerTickEndEvent event) {
        for (final ZombiesWorld world : ZombiesPlugin.INSTANCE.getGameWorlds()) {
            final int round = world.get(ZombiesWorld.ROUND);
            final int waveCount = world.get(ZombiesWorld.WAVE);
            final int nextWaveTime = world.get(ZombiesWorld.NEXT_WAVE_TIME);

            if (nextWaveTime > 0) {
                world.set(ZombiesWorld.NEXT_WAVE_TIME, nextWaveTime - 1);
                continue;
            }
            final SpawnRate spawnRate = SpawnRate.SPAWN_RATES.get(round - 1);
            final int remainingZombies = world.get(ZombiesWorld.REMAINING_ZOMBIES);
            if (remainingZombies == 0) {
                continue;
            }

            final List<Window> availableWindows = world.getAvailableWindows();
            final RandomGenerator rnd = ThreadLocalRandom.current();
            final List<ZombieType> types = new ArrayList<>(List.of());

            final AtomicInteger j = new AtomicInteger();
            j.set(1);

            spawnRate.waves().get(waveCount)
                    .zombies().forEach((type, count) -> {
                for (int i = 0; i < count; i++) {
                    types.add(rnd.nextInt(j.getAndIncrement()), type);
                }
            });

            final int remainingThisWave = spawnRate.waves().get(waveCount).getNumberOfZombies();
            final int windowsNum = availableWindows.size();
            final int n = remainingThisWave / windowsNum;

            for (int i = 0; i < remainingThisWave; ++i) {
                final Window window = availableWindows.get(i % windowsNum);
                final Location spawnLocation = window.spawnLocation.toBukkit(world.getBukkit());
                final Zombie zombie = world.spawnZombie(spawnLocation, types.get(i));

                zombie.set(Zombie.CLOSEST_POINT, window.closestPointId);
                zombie.set(Zombie.IN_WINDOW, true);
                zombie.set(Zombie.SPAWN_POS_Y, window.spawnLocation.y());

                final Mob mob = (Mob) zombie.getEntity();

            }

            world.set(ZombiesWorld.REMAINING_ZOMBIES, remainingZombies - remainingThisWave);
            final int maxWave = spawnRate.waves().size() - 1;
            if (waveCount != maxWave) {
                world.set(ZombiesWorld.WAVE, waveCount + 1);
                world.set(ZombiesWorld.NEXT_WAVE_TIME, spawnRate.waves().get(waveCount + 1).waveDelay());
            }

        }
    }
}
