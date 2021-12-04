package io.github.flemmli97.mobbattle.forge.client;

import io.github.flemmli97.mobbattle.client.gui.GuiArmor;
import io.github.flemmli97.mobbattle.client.gui.GuiEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.DistExecutor;

public class ClientOpenGuiHelper {

    public static void openEffectGUI() {
        Minecraft.getInstance().setScreen(new GuiEffect());
    }
}
