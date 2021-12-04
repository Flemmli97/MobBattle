package io.github.flemmli97.mobbattle.client.fabric;

import io.github.flemmli97.mobbattle.client.gui.GuiEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class CrossClientHandlerImpl {

    public static void openEffectGui() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            Minecraft.getInstance().setScreen(new GuiEffect());
    }

    public static boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.matches(keyCode, scanCode);
    }
}
