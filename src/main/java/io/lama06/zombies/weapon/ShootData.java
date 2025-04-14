package io.lama06.zombies.weapon;

public record ShootData(int bullets,
                        int delay,
                        double precision,
                        int pierce,
                        int chain_reaction,
                        int explosion) { }
