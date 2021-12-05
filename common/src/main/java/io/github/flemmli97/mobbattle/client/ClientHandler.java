package io.github.flemmli97.mobbattle.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.flemmli97.mobbattle.client.gui.GuiEffect;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ClientHandler {

    public static void openEffectGui() {
        Minecraft.getInstance().setScreen(new GuiEffect());
    }

    @ExpectPlatform
    public static boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        throw new AssertionError();
    }
}
