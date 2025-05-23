package io.github.flemmli97.mobbattle.platform;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.client.KeyMapping;

public interface ClientPlatform {

    ClientPlatform INSTANCE = MobBattle.getPlatformInstance(ClientPlatform.class,
            "io.github.flemmli97.mobbattle.fabric.platform.ClientPlatformImpl",
            "io.github.flemmli97.mobbattle.forge.platform.ClientPlatformImpl");

    boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode);

}
