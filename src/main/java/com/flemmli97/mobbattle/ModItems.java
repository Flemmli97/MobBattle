package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import com.flemmli97.mobbattle.items.MobArmor;
import com.flemmli97.mobbattle.items.MobArmy;
import com.flemmli97.mobbattle.items.MobEffect;
import com.flemmli97.mobbattle.items.MobEffectGive;
import com.flemmli97.mobbattle.items.MobEquip;
import com.flemmli97.mobbattle.items.MobGroup;
import com.flemmli97.mobbattle.items.MobHeal;
import com.flemmli97.mobbattle.items.MobKill;
import com.flemmli97.mobbattle.items.MobMount;
import com.flemmli97.mobbattle.items.MobStick;
import com.flemmli97.mobbattle.items.entitymanager.Team;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobBattle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    public static IItemTier mob_mat = new IItemTier() {

        @Override
        public int getMaxUses() {
            return -1;
        }

        @Override
        public float getEfficiency() {
            return 1;
        }

        @Override
        public float getAttackDamage() {
            return -5;
        }

        @Override
        public int getHarvestLevel() {
            return 0;
        }

        @Override
        public int getEnchantability() {
            return 0;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.EMPTY;
        }
    };

    public static Item mobStick;
    public static Item mobKill;
    public static Item mobHeal;
    public static Item mobEffect;
    public static Item mobGroup;
    public static Item mobArmor;
    public static Item mobMount;
    public static Item mobArmy;
    public static Item mobEquip;
    public static Item mobEffectGiver;
    public static Item spawner;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                mobStick = new MobStick().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_stick")),
                mobKill = new MobKill().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_kill")),
                mobHeal = new MobHeal().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_heal")),
                mobEffect = new MobEffect().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_effect")),
                mobGroup = new MobGroup().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_group")),
                mobArmor = new MobArmor().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_armor")),
                mobMount = new MobMount().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_mount")),
                mobArmy = new MobArmy().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_army")),
                mobEquip = new MobEquip().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_equip")),
                mobEffectGiver = new MobEffectGive().setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_effect_give")),
                spawner = new ItemExtendedSpawnEgg().setRegistryName(new ResourceLocation(MobBattle.MODID, "egg_ex"))
        );
        DispenserBlock.registerDispenseBehavior(ModItems.spawner, (source, stack) -> {
            Direction enumfacing = source.getBlockState().get(DispenserBlock.FACING);
            double x = source.getX() + enumfacing.getXOffset();
            double y = source.getBlockPos().getY() + enumfacing.getYOffset() + 0.2;
            double z = source.getZ() + enumfacing.getZOffset();
            BlockPos blockpos = new BlockPos(x, y, z);
            Entity entity = ItemExtendedSpawnEgg.spawnEntity(source.getWorld(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D);
            if (entity != null) {
                stack.shrink(1);
                if (stack.hasDisplayName() && entity instanceof CreatureEntity) {
                    Team.updateEntity(stack.getDisplayName().getUnformattedComponentText(), (CreatureEntity) entity);
                }
            }
            return stack;
        });
    }
}
