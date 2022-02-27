package io.github.flemmli97.mobbattle.fabric.client;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.gui.GuiArmor;
import io.github.flemmli97.mobbattle.client.gui.MultiItemColor;
import io.github.flemmli97.mobbattle.fabric.ModItems;
import io.github.flemmli97.mobbattle.fabric.ModMenuType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class MobBattleFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(new MultiItemColor(), ModItems.spawner);
        WorldRenderEvents.END.register(ClientEvents::render);
        ScreenRegistry.register(ModMenuType.armorMenu, GuiArmor::new);
        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS)
                .register(((atlas, reg) -> reg.register(new ResourceLocation(MobBattle.MODID, "gui/armor_slot_sword"))));
    }
}
