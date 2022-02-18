package io.github.flemmli97.mobbattle.platform;

import net.minecraft.client.KeyMapping;

public abstract class ClientPlatform {

    protected static ClientPlatform INSTANCE;

    public static ClientPlatform instance() {
        return INSTANCE;
    }

    public abstract boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode);

}
