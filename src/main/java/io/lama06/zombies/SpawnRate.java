package io.lama06.zombies;

import io.lama06.zombies.zombie.ZombieType;

import java.util.List;
import java.util.Map;

public record SpawnRate(List<SpawnRateWave> waves) {
    public static final List<SpawnRate> SPAWN_RATES = List.of(
            // 1:
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    )),
                    new SpawnRateWave(15 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    ))

            )),
            // 2:
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE2, 5
                    )),
                    new SpawnRateWave(15 * 20, Map.of(
                            ZombieType.ZOMBIE2, 5
                    ))

            )),
            // 3
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE2, 4,
                            ZombieType.PIG_ZOMBIE, 3
                    )),
                    new SpawnRateWave(15 * 20, Map.of(
                            ZombieType.ZOMBIE2, 2,
                            ZombieType.PIG_ZOMBIE, 2
                    ))
            )),
            // 4
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE2, 2,
                            ZombieType.ZOMBIE3, 3,
                            ZombieType.ZOMBIE_WOLF, 3

                            )),
                    new SpawnRateWave(20 * 20, Map.of(
                            ZombieType.ZOMBIE3, 3,
                            ZombieType.ZOMBIE_WOLF, 2
                    ))
            )),
            // 5
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    )),
                    new SpawnRateWave(20 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    ))
            )),
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    )),
                    new SpawnRateWave(20 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    ))

            )),
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    )),
                    new SpawnRateWave(15 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    ))

            )),
            new SpawnRate(List.of(
                    new SpawnRateWave(3 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    )),
                    new SpawnRateWave(15 * 20, Map.of(
                            ZombieType.ZOMBIE1, 4
                    ))

            ))

    );

    public int getNumberOfZombies() {
        int n = 0;
        for (final SpawnRateWave wave : waves) n += wave.zombies().values().stream().mapToInt(j -> j).sum();
        return n;
    }
}