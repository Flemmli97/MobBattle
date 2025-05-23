package io.github.flemmli97.mobbattle.client;

import io.github.flemmli97.mobbattle.client.gui.GuiEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

public class ClientHandler {

    public static void openEffectGui() {
        Minecraft.getInstance().setScreen(new GuiEffect());
    }

    public static void openSpawneggGui(InteractionHand hand) {

    }
}
