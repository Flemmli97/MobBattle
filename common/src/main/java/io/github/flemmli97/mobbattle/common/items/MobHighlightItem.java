package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

public interface MobHighlightItem {

    default Entity getDefaultHover(EntityHitResult result) {
        return CrossPlatformStuff.INSTANCE.tryGetLivingEntity(result.getEntity());
    }
}
