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

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobBattle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

	public static IItemTier mob_mat = new IItemTier() {
		@Override
		public int getMaxUses() {return -1;}
		@Override
		public float getEfficiency() {return 1;}
		@Override
		public float getAttackDamage() {return -5;}
		@Override
		public int getHarvestLevel() {return 0;}
		@Override
		public int getEnchantability() {return 0;}
		@Override
		public Ingredient getRepairMaterial() {return Ingredient.EMPTY;}};
		
	public static Item mobStick = new MobStick();
	public static Item mobKill = new MobKill();
	public static Item mobHeal = new MobHeal();
	public static Item mobEffect = new MobEffect();
	public static Item mobGroup = new MobGroup();
	public static Item mobArmor = new MobArmor();
	public static Item mobMount = new MobMount();
	public static Item mobArmy = new MobArmy();
	public static Item mobEquip = new MobEquip();
	public static Item mobEffectGiver = new MobEffectGive();
	public static Item spawner = new ItemExtendedSpawnEgg();
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
	    event.getRegistry().register(mobStick);
	    event.getRegistry().register(mobKill);
	    event.getRegistry().register(mobHeal);
	    event.getRegistry().register(mobEffect);
	    event.getRegistry().register(mobGroup);
	    event.getRegistry().register(mobArmor);
	    event.getRegistry().register(mobArmy);
	    event.getRegistry().register(mobMount);
	    event.getRegistry().register(mobEquip);
	    event.getRegistry().register(mobEffectGiver);
	    event.getRegistry().register(spawner);
	    BlockDispenser.registerDispenseBehavior(ModItems.spawner, new BehaviorDefaultDispenseItem()
        {
	        @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = (EnumFacing)source.getBlockState().get(BlockDispenser.FACING);
                double x = source.getX() + (double)enumfacing.getXOffset();
                double y = (double)(source.getBlockPos().getY() + enumfacing.getYOffset() + 0.2F);
                double z = source.getZ() + (double)enumfacing.getZOffset();
                BlockPos blockpos = new BlockPos(x,y,z);
                Entity entity = ItemExtendedSpawnEgg.spawnEntity(source.getWorld(), stack, blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D);
                if (entity != null)
                {
                    stack.shrink(1);
                    if(stack.hasDisplayName() && entity instanceof EntityCreature)
            		{
                        Team.updateEntity(stack.getDisplayName().getUnformattedComponentText(), (EntityCreature) entity);
            		}
                }
                return stack;
            }
        });
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static final void registerTextureSprite(TextureStitchEvent.Pre event)
	{
		ResourceLocation res = new ResourceLocation(MobBattle.MODID + ":gui/armor_slot_sword");
		event.getMap().registerSprite(Minecraft.getInstance().getResourceManager(), res);
	}
}
