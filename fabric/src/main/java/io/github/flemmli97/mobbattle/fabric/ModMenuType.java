package io.github.flemmli97.mobbattle.fabric;


import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModMenuType {

    public static MenuType<ContainerArmor> armorMenu;

    public static void register() {
        armorMenu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(MobBattle.MODID, "armor_menu"), ContainerArmor::new);
    }
}
