package io.github.flemmli97.mobbattle.platform;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.ItemStack;

public interface ClientPlatform {

    ClientPlatform INSTANCE = MobBattle.getPlatformInstance(ClientPlatform.class,
            "io.github.flemmli97.mobbattle.fabric.platform.ClientPlatformImpl",
            "io.github.flemmli97.mobbattle.forge.platform.ClientPlatformImpl");

    boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode);

    void itemStackUpdatePacket(EffectComponent effect);

    void sendEquipMessage(ItemStack stack, int entityId, int slot);

}
