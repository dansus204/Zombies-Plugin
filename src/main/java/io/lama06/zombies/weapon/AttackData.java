package io.lama06.zombies.weapon;

public record AttackData(double damage,
                         boolean fire,
                         boolean stun,
                         boolean poison,
                         int gold,
                         int crit_gold) { }
