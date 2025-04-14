package io.lama06.zombies;

import io.lama06.zombies.zombie.ZombieType;

import java.util.List;
import java.util.Map;

public record SpawnRateWave(int waveDelay, Map<ZombieType, Integer> zombies, ZombieType boss) {

    public SpawnRateWave(final int waveDelay, final Map<ZombieType, Integer> zombies) {
        this(waveDelay, zombies, null);
    }

    public int getNumberOfZombies() {
        return zombies.values().stream().mapToInt(i -> i).sum();
    }
}