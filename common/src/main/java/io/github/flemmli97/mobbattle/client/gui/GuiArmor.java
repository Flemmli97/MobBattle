package io.github.flemmli97.mobbattle.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;

public class GuiArmor extends AbstractContainerScreen<ContainerArmor> {

    private final Component chatComponent;
    private static final ResourceLocation armorGui = new ResourceLocation(MobBattle.MODID, "textures/gui/armor.png");

    public GuiArmor(ContainerArmor container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.chatComponent = title;
    }

    @Override
    protected void renderLabels(PoseStack matrix, int mouseX, int mouseY) {
        String s = this.chatComponent.getContents();
        this.font.draw(matrix, s, this.imageWidth / 2 - this.font.width(s) / 2, 6, 4210752);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, armorGui);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
