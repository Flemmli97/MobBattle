package io.github.flemmli97.mobbattle.fabric.platform;

import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import net.minecraft.client.KeyMapping;

public class ClientPlatformImpl implements ClientPlatform {

    @Override
    public boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.matches(keyCode, scanCode);
    }
}
