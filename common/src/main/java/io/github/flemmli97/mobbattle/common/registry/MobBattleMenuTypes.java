package io.github.flemmli97.mobbattle.common.registry;

import io.github.flemmli97.mobbattle.common.inv.ContainerArmor;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MobBattleMenuTypes {

    public static final Supplier<MenuType<ContainerArmor>> ARMOR_MENU = CrossPlatformStuff.INSTANCE.registerMenu("armor_menu", ContainerArmor::new, ByteBufCodecs.INT.cast());

    public static void init() {

    }
}
