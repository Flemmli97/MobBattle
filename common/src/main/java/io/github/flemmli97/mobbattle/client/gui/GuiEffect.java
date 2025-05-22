package io.github.flemmli97.mobbattle.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiEffect extends Screen {

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID, "textures/gui/effect.png");
    private final int xSize = 200;
    private final int ySize = 100;
    private EditBox potion;
    private EditBox duration;
    private EditBox amplifier;
    private ButtonCheck button;
    private final ItemStack stack;

    private final Component durationTxt = new TranslatableComponent("mobbattle.gui.duration");
    private final Component amplifierTxt = new TranslatableComponent("mobbattle.gui.amplifier");
    private final Component particleTxt = new TranslatableComponent("mobbattle.gui.particle");

    public GuiEffect() {
        super(new TranslatableComponent("mobbattle.gui.potion"));
        this.stack = Minecraft.getInstance().player.getMainHandItem();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potion = new SuggestionEditBox(this.font, i + 30, j + 21, 140, 14, TextComponent.EMPTY, 5, false, Registry.MOB_EFFECT.keySet());
        this.potion.setResponder(s -> {
            CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
            compound.putString(MobBattle.MODID + ":potion", s);
            GuiEffect.this.stack.setTag(compound);
            try {
                ResourceLocation id = new ResourceLocation(s);
                if (Registry.MOB_EFFECT.containsKey(id)) {
                    this.potion.setTextColor(0xE0E0E0);
                } else {
                    this.potion.setTextColor(0xFF0000);
                }
            } catch (Exception e) {
                this.potion.setTextColor(0xFF0000);
            }
        });
        this.potion.setMaxLength(35);
        this.potion.setEditable(true);
        this.potion.setValue(this.stack.hasTag() ? this.stack.getTag().getString(MobBattle.MODID + ":potion") : "");

        this.duration = new EditBox(this.font, i + 30, j + 62, 52, 10, TextComponent.EMPTY) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            compound.putInt(MobBattle.MODID + ":duration", Integer.parseInt(this.getValue()));
                            GuiEffect.this.stack.setTag(compound);
                        } catch (NumberFormatException e) {
                            MobBattle.logger.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.duration.setMaxLength(7);
        this.duration.setEditable(true);
        this.duration.setValue(this.stack.hasTag() ? "" + this.stack.getTag().getInt(MobBattle.MODID + ":duration") : "");
        this.addRenderableWidget(this.duration);

        this.amplifier = new EditBox(this.font, i + 108, j + 62, 23, 10, TextComponent.EMPTY) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            int i = Integer.parseInt(this.getValue());
                            if (i > 255)
                                this.setValue("" + 255);
                            compound.putInt(MobBattle.MODID + ":amplifier", Integer.parseInt(this.getValue()));
                            GuiEffect.this.stack.setTag(compound);
                        } catch (NumberFormatException e) {
                            MobBattle.logger.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.amplifier.setMaxLength(3);
        this.amplifier.setEditable(true);
        this.amplifier.setValue(this.stack.hasTag() ? "" + this.stack.getTag().getInt(MobBattle.MODID + ":amplifier") : "");
        this.addRenderableWidget(this.amplifier);

        this.button = new ButtonCheck(i + 160, j + 62, (button) -> {
            ButtonCheck check = (ButtonCheck) button;
            check.checkUncheck(!check.isChecked());
            CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
            compound.putBoolean(MobBattle.MODID + ":show", ((ButtonCheck) button).isChecked());
            GuiEffect.this.stack.setTag(compound);
        });
        this.addRenderableWidget(this.button);
        this.button.checkUncheck(this.stack.hasTag() && this.stack.getTag().getBoolean(MobBattle.MODID + ":show"));
        this.addRenderableWidget(this.potion);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        boolean texFocused = this.potion.isFocused() || this.amplifier.isFocused() || this.duration.isFocused();
        if ((keyCode == 256 && this.shouldCloseOnEsc()) || (!texFocused && ClientPlatform.INSTANCE.keyMatches(this.minecraft.options.keyInventory, keyCode, scanCode))) {
            if (this.stack.hasTag())
                CrossPlatformStuff.INSTANCE.itemStackUpdatePacket(this.stack.getTag());
            this.onClose();
            return true;
        } else
            return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.potion.canConsumeInput() && this.potion.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.potion.canConsumeInput() && this.potion.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
        this.font.draw(matrix, this.getTitle(), this.potion.x, j + 10, 1);
        int y = j + 62 - 14;
        this.font.draw(matrix, this.durationTxt, this.duration.x, y, 1);
        float txtX = this.amplifier.x + this.amplifier.getWidth() * 0.5f;
        float partLen = this.font.width(this.amplifierTxt) * 0.5f;
        this.font.draw(matrix, this.amplifierTxt, (txtX - partLen), y, 1);
        txtX = this.button.x + this.button.getWidth() * 0.5f;
        partLen = this.font.width(this.particleTxt) * 0.5f;
        this.font.draw(matrix, this.particleTxt, (txtX - partLen), y, 1);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }
}
