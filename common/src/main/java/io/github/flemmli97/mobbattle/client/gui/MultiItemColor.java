package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.tenshilib.common.item.SpawnEgg;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Optional;

public class MultiItemColor implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        ResourceLocation id = ItemExtendedSpawnEgg.getNamedIdFrom(stack);
        if (id != null) {
            if (id.equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITHER))) {
                return tintIndex == 0 ? 0x161616 : 0x424242;
            }
            if (id.equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.IRON_GOLEM))) {
                return tintIndex == 0 ? 0xd0b096 : 0x658932;
            }
            if (id.equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SNOW_GOLEM))) {
                return tintIndex == 0 ? 0xffffff : 0xe3901d;
            }
            if (id.equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.GIANT))) {
                id = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE);
            }
            if (id.equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ILLUSIONER))) {
                return tintIndex == 0 ? 0x135893 : 0x848989;
            }
            SpawnEggItem vanillaEgg = SpawnEggItem.byId(BuiltInRegistries.ENTITY_TYPE.get(id));
            if (vanillaEgg != null)
                return vanillaEgg.getColor(tintIndex);

            if (MobBattle.tenshiLib) {
                Optional<SpawnEgg> egg = SpawnEgg.fromID(id);
                if (egg.isPresent())
                    return egg.get().getColor(stack, tintIndex);
            }
        }
        return -1;
    }
}
