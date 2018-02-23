package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.items.MobEffect;
import com.flemmli97.mobbattle.items.MobGroup;
import com.flemmli97.mobbattle.items.MobHeal;
import com.flemmli97.mobbattle.items.MobKill;
import com.flemmli97.mobbattle.items.MobStick;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {

	public static Item mobStick;
	public static Item mobKill;
	public static Item mobHeal;
	public static Item mobEffect;
	public static Item mobGroup;
	public static ToolMaterial mob_mat = EnumHelper.addToolMaterial("mob_mat", 0, -1, 1.0F, -5.0F, 0);
	
	public static final void init()
	{
		GameRegistry.registerItem(mobStick = new MobStick(mob_mat), "mob_stick");
		GameRegistry.registerItem(mobKill = new MobKill(mob_mat), "mob_kill");
		GameRegistry.registerItem(mobHeal = new MobHeal(mob_mat), "mob_heal");
		GameRegistry.registerItem(mobEffect = new MobEffect(mob_mat), "mob_effect");
		GameRegistry.registerItem(mobGroup = new MobGroup(mob_mat), "mob_group");
	}
}
