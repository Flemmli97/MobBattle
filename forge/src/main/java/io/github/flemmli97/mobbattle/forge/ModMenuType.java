package io.github.flemmli97.mobbattle.forge;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuType {

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MobBattle.MODID);

    public static final RegistryObject<MenuType<ContainerArmor>> armorMenu = MENU_TYPE.register("armor_menu", () -> IForgeMenuType.create(ContainerArmor::new));
}
