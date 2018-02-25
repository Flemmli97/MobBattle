package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.items.MobArmor;
import com.flemmli97.mobbattle.items.MobArmy;
import com.flemmli97.mobbattle.items.MobEffect;
import com.flemmli97.mobbattle.items.MobGroup;
import com.flemmli97.mobbattle.items.MobHeal;
import com.flemmli97.mobbattle.items.MobKill;
import com.flemmli97.mobbattle.items.MobMount;
import com.flemmli97.mobbattle.items.MobStick;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = MobBattle.MODID)
public class ModItems {

	public static ToolMaterial mob_mat = EnumHelper.addToolMaterial("mob_mat", 0, -1, 1.0F, -5.0F, 0);
	public static Item mobStick = new MobStick();
	public static Item mobKill = new MobKill();
	public static Item mobHeal = new MobHeal();
	public static Item mobEffect = new MobEffect();
	public static Item mobGroup = new MobGroup();
	public static Item mobArmor = new MobArmor();
	public static Item mobMount = new MobMount();
	public static Item mobArmy = new MobArmy();

	@SubscribeEvent
	public static final void registerItems(RegistryEvent.Register<Item> event) {
	    event.getRegistry().register(mobStick);
	    event.getRegistry().register(mobKill);
	    event.getRegistry().register(mobHeal);
	    event.getRegistry().register(mobEffect);
	    event.getRegistry().register(mobGroup);
	    event.getRegistry().register(mobArmor);
	    event.getRegistry().register(mobArmy);
	    event.getRegistry().register(mobMount);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static final void initModels(ModelRegistryEvent event)
	{
		((MobStick) mobStick).initModel();
		((MobEffect) mobEffect).initModel();
		((MobKill) mobKill).initModel();
		((MobHeal) mobHeal).initModel();
		((MobGroup) mobGroup).initModel();
		((MobArmor) mobArmor).initModel();
		((MobMount) mobMount).initModel();
		((MobArmy) mobArmy).initModel();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static final void registerTextureSprite(TextureStitchEvent.Pre event)
	{
		ResourceLocation res = new ResourceLocation(MobBattle.MODID + ":gui/armor_slot_sword");
		event.getMap().registerSprite(res);
	}
}
