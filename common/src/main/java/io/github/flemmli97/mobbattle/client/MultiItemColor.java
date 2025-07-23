package io.github.flemmli97.mobbattle.client;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.tenshilib.common.item.SpawnEgg;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Optional;

public class MultiItemColor implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return ItemExtendedSpawnEgg.getType(stack)
                .map(type -> {
                    if (type == EntityType.WITHER) {
                        return tintIndex == 0 ? 0xff161616 : 0xff424242;
                    }
                    if (type == EntityType.GIANT) {
                        type = EntityType.ZOMBIE;
                    }
                    if (type == EntityType.ILLUSIONER) {
                        return tintIndex == 0 ? 0xff135893 : 0xff848989;
                    }
                    SpawnEggItem vanillaEgg = SpawnEggItem.byId(type);
                    if (vanillaEgg != null)
                        return FastColor.ABGR32.opaque(vanillaEgg.getColor(tintIndex));
                    if (MobBattle.tenshiLib) {
                        Optional<SpawnEgg> egg = SpawnEgg.fromType(type);
                        if (egg.isPresent())
                            return FastColor.ABGR32.opaque(egg.get().getColor(stack, tintIndex));
                    }
                    return -1;
                }).orElse(-1);
    }
}
