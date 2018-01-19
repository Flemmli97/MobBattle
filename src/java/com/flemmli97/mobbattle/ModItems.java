package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.items.MobArmor;
import com.flemmli97.mobbattle.items.MobEffect;
import com.flemmli97.mobbattle.items.MobGroup;
import com.flemmli97.mobbattle.items.MobHeal;
import com.flemmli97.mobbattle.items.MobKill;
import com.flemmli97.mobbattle.items.MobStick;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

	public static ToolMaterial mob_mat = EnumHelper.addToolMaterial("mob_mat", 0, -1, 1.0F, -5.0F, 0);

	public static Item mobStick;
	public static Item mobKill;
	public static Item mobHeal;
	public static Item mobEffect;
	public static Item mobGroup;
	public static Item mobArmor;
	
	public static final void init() {
		
		mobStick = new MobStick();
		mobKill = new MobKill();
		mobHeal = new MobHeal();
		mobEffect = new MobEffect();
		mobGroup = new MobGroup();
		mobArmor = new MobArmor();
	}
	
	@SideOnly(Side.CLIENT)
	public static final void initModels()
	{
		((MobStick) mobStick).initModel();
		((MobEffect) mobEffect).initModel();
		((MobKill) mobKill).initModel();
		((MobHeal) mobHeal).initModel();
		((MobGroup) mobGroup).initModel();
		((MobArmor) mobArmor).initModel();
	}
}
