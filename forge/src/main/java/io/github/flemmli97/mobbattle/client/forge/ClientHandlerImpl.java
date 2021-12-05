package io.github.flemmli97.mobbattle.client.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ClientHandlerImpl {

    public static boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }
}
