package io.github.flemmli97.mobbattle.fabric.platform;

import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.network.EffectGiveUpdate;
import io.github.flemmli97.mobbattle.network.EquipMessage;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.ItemStack;

public class ClientPlatformImpl implements ClientPlatform {

    @Override
    public boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.matches(keyCode, scanCode);
    }

    @Override
    public void itemStackUpdatePacket(EffectComponent effect) {
        ClientPlayNetworking.send(new EffectGiveUpdate(effect));
    }

    @Override
    public void sendEquipMessage(ItemStack stack, int entityId, int slot) {
        ClientPlayNetworking.send(new EquipMessage(stack, entityId, slot));
    }
}
