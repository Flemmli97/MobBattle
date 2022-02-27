package io.github.flemmli97.mobbattle.forge.platform;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import net.minecraft.client.KeyMapping;

public class ClientPlatformImpl implements ClientPlatform {

    @Override
    public boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }
}
