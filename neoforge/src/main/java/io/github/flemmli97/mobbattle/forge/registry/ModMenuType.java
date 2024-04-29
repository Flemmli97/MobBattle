package io.github.flemmli97.mobbattle.forge.registry;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuType {

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Registries.MENU, MobBattle.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerArmor>> ARMOR_MENU = MENU_TYPE.register("armor_menu", () -> new MenuType<>((IContainerFactory<ContainerArmor>) ContainerArmor::new, FeatureFlagSet.of()));
}
