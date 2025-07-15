package io.github.flemmli97.mobbattle.fabric.registry;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.inv.ContainerArmor;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;

public class ModMenuType {

    public static MenuType<ContainerArmor> armorMenu;

    public static void register() {
        armorMenu = Registry.register(BuiltInRegistries.MENU, MobBattle.of("armor_menu"),
                new ExtendedScreenHandlerType<>((i, inv, ent) -> new ContainerArmor(i, inv, inv.player.level().getEntity(ent)), ByteBufCodecs.INT.cast().mapStream(s -> s)));
    }
}
