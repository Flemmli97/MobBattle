package io.github.flemmli97.mobbattle.client.forge;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.flemmli97.mobbattle.forge.client.ClientOpenGuiHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CrossClientHandlerImpl {

    public static void openEffectGui() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientOpenGuiHelper::openEffectGUI);
    }

    public static boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }
}
