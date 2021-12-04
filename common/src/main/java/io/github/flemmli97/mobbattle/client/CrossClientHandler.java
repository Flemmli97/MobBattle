package io.github.flemmli97.mobbattle.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.KeyMapping;

public class CrossClientHandler {

    @ExpectPlatform
    public static void openEffectGui() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        throw new AssertionError();
    }
}
