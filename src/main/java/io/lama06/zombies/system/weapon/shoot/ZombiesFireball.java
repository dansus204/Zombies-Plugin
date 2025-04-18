package io.lama06.zombies.system.weapon.shoot;

import io.lama06.zombies.ZombiesWorld;
import io.lama06.zombies.data.AttributeId;
import io.lama06.zombies.data.ComponentId;
import io.lama06.zombies.data.Storage;
import io.lama06.zombies.data.StorageSession;
import io.lama06.zombies.util.pdc.EnumPersistentDataType;
import io.lama06.zombies.weapon.WeaponType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public final class ZombiesFireball extends Storage {
    public static final AttributeId<WeaponType> FIREBALL_WEAPON = new AttributeId<>("fireball_weapon", new EnumPersistentDataType<>(WeaponType.class));

    private final Entity entity;

    public ZombiesFireball(final Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public ZombiesWorld getWorld() {
        return new ZombiesWorld(entity.getWorld());
    }

    @Override
    protected StorageSession startSession() {
        return entity::getPersistentDataContainer;
    }

}
