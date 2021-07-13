package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import com.flemmli97.tenshilib.common.item.SpawnEgg;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class MultiItemColor implements IItemColor {

    //private static Map<ResourceLocation, AnimalContainer> animaniaMap;

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        ResourceLocation id = ItemExtendedSpawnEgg.getNamedIdFrom(stack);
        if (id != null) {
            if (id.equals(EntityType.WITHER.getRegistryName())) {
                return tintIndex == 0 ? 0x161616 : 0x424242;
            }
            if (id.equals(EntityType.IRON_GOLEM.getRegistryName())) {
                return tintIndex == 0 ? 0xd0b096 : 0x658932;
            }
            if (id.equals(EntityType.SNOW_GOLEM.getRegistryName())) {
                return tintIndex == 0 ? 0xffffff : 0xe3901d;
            }
            if (id.equals(EntityType.GIANT.getRegistryName())) {
                id = EntityType.ZOMBIE.getRegistryName();
            }
            if (id.equals(EntityType.ILLUSIONER.getRegistryName())) {
                return tintIndex == 0 ? 0x135893 : 0x848989;
            }
            SpawnEggItem vanillaEgg = SpawnEggItem.getEgg(ForgeRegistries.ENTITIES.getValue(id));
            if (vanillaEgg != null)
                return vanillaEgg.getColor(tintIndex);

            if (MobBattle.tenshiLib) {
                Optional<SpawnEgg> egg = SpawnEgg.fromID(id);
                if (egg.isPresent())
                    return egg.get().getColor(tintIndex);
            }
            /*
             * if(eggInfo==null && CommonProxy.fate) eggInfo = SpawnEntityCustomList.entityEggs.get(id); if(eggInfo == null &&
             * CommonProxy.runecraftory) eggInfo = EntitySpawnEggList.entityEggs.get(id); if(eggInfo==null && CommonProxy.animania)
             * { if(animaniaMap==null) { animaniaMap = new HashMap<>(); for(Entry<AnimalContainer, EntityEntry> e :
             * EntityEggHandler.ENTITY_MAP.entrySet()) { animaniaMap.put(e.getValue().getRegistryName(), e.getKey()); } }
             * if(animaniaMap.containsKey(id)) { AnimalContainer animal = animaniaMap.get(id); if
             * (ItemEntityEgg.ANIMAL_USES_COLOR.containsKey(animal) && ItemEntityEgg.ANIMAL_USES_COLOR.get(animal)) { return
             * tintIndex == 0 ? ItemEntityEgg.ANIMAL_COLOR_PRIMARY.get(animal) : ItemEntityEgg.ANIMAL_COLOR_SECONDARY.get(animal); }
             * } } if(eggInfo==null && CommonProxy.mca) { ResourceLocation villager=EntityList.getKey(EntityVillagerMCA.class);
             * ResourceLocation grimReaper=EntityList.getKey(EntityGrimReaper.class); if(villager!=null && id.equals(villager)) {
             * int i = stack.getTag().getCompound(ItemExtendedSpawnEgg.tagString).contains("GENDER")?stack.getTag().getCompound(
             * ItemExtendedSpawnEgg.tagString).getInteger("GENDER"):
             * stack.getTag().getCompound(ItemExtendedSpawnEgg.tagString).contains("MCAGender")?stack.getTag().getCompound(
             * ItemExtendedSpawnEgg.tagString).getInteger("MCAGender"):0; EnumGender gender = EnumGender.byId(i); return
             * tintIndex==0? (gender==EnumGender.MALE?0x336f90:0xff8d8d):(gender==EnumGender.MALE?0x4c97f9:0xffb4b4); }
             * if(grimReaper!=null && id.equals(grimReaper)) { return tintIndex==0?0x000000:0x1d1d1d; } }
             */
        }
        return -1;
    }
}
