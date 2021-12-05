package io.github.flemmli97.mobbattle.client.fabric;

import net.minecraft.client.KeyMapping;

public class ClientHandlerImpl {

    public static boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.matches(keyCode, scanCode);
    }
}
