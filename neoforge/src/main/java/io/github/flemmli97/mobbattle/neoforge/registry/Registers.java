package io.github.flemmli97.mobbattle.neoforge.registry;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Registers {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MobBattle.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MobBattle.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Registries.MENU, MobBattle.MODID);

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
        ITEMS.register(modBus);
        MENU_TYPE.register(modBus);
    }
}
