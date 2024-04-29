package io.github.flemmli97.mobbattle.forge.platform;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.network.EffectGiveUpdate;
import io.github.flemmli97.mobbattle.network.EquipMessage;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class ClientPlatformImpl implements ClientPlatform {

    @Override
    public boolean keyMatches(KeyMapping mapping, int keyCode, int scanCode) {
        return mapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }

    @Override
    public void sendEquipMessage(ItemStack stack, int entityId, int slot) {
        Minecraft.getInstance().getConnection().send(new EquipMessage(stack, entityId, slot));
    }

    @Override
    public void itemStackUpdatePacket(EffectComponent effect) {
        Minecraft.getInstance().getConnection().send(new EffectGiveUpdate(effect));
    }
}
