package com.flemmli97.mobbattle.client;

import com.flemmli97.mobbattle.client.gui.GuiArmor;
import com.flemmli97.mobbattle.client.gui.GuiEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class ClientOpenGuiHelper {

    public static DistExecutor.SafeRunnable openArmorGUI(int windowID, int entityID) {
        return ()-> {
            PlayerEntity player = Minecraft.getInstance().player;
            Entity e = player.world.getEntityByID(entityID);
            if (!(e instanceof MobEntity))
                return;
            GuiArmor screen = new GuiArmor(windowID, player.inventory, (MobEntity) e);
            Minecraft.getInstance().player.openContainer = screen.getContainer();
            Minecraft.getInstance().displayGuiScreen(screen);
        };
    }

    public static void openEffectGUI() {
        Minecraft.getInstance().displayGuiScreen(new GuiEffect());
    }

    public PlayerEntity getPlayer(Supplier<Context> ctx) {
        return Minecraft.getInstance().player;
    }
}
