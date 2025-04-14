package io.lama06.zombies;

import io.lama06.zombies.data.AttributeId;
import io.lama06.zombies.data.Storage;
import io.lama06.zombies.data.StorageSession;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class ZombiesEntity extends Storage {
    public static final AttributeId<Boolean> IS_NOT_VANILLA = new AttributeId<>("is_not_vanilla", PersistentDataType.BOOLEAN);
    public static final AttributeId<Integer> SHOP_ID = new AttributeId<>("shop_id", PersistentDataType.INTEGER);


    private final Entity entity;


    public ZombiesEntity(final Entity entity) {
        this.entity = entity;
    }

    public ZombiesWorld getWorld() {
        return new ZombiesWorld(entity.getWorld());
    }

    public boolean isNotVanilla() {
        return Objects.requireNonNullElse(get(IS_NOT_VANILLA), false);
    }


    @Override
    protected StorageSession startSession() {
        return entity::getPersistentDataContainer;
    }

}
