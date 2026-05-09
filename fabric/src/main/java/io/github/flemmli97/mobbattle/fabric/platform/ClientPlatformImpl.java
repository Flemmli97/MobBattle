package io.github.flemmli97.mobbattle.fabric.platform;

import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

public class ClientPlatformImpl implements ClientPlatform {

    @Override
    public boolean keyMatches(KeyMapping mapping, KeyEvent event) {
        return mapping.matches(event);
    }
}
