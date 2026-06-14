package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.components.PerimeterComponent;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.utils.PerimeterData;
import io.github.flemmli97.mobbattle.network.C2SPerimeterComponent;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class PerimeterSettingScreen extends Screen {

    private final Player player;
    private final InteractionHand hand;

    private int leftPos, topPos;
    private final int sizeX = 160;
    private final int sizeY = 120;

    private PerimeterComponent perimeter;
    private EditBox outerBox;

    public PerimeterSettingScreen(InteractionHand hand) {
        super(Component.empty());
        this.hand = hand;
        this.player = Minecraft.getInstance().player;
        ItemStack stack = this.player.getItemInHand(this.hand);
        this.perimeter = stack.getOrDefault(MobBattleDataComponents.PERIMETER.get(), PerimeterComponent.DEFAULT);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = this.width / 2 - (this.sizeX / 2);
        this.topPos = this.height / 2 - (this.sizeY / 2);
        this.buttons();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(this.leftPos, this.topPos, this.leftPos + this.sizeX, this.topPos + this.sizeY, 0xc0101010, 0xc0101010);
        int xPadding = 16;
        int yOff = xPadding;
        guiGraphics.text(this.font, Component.translatable("mobbattle.gui.perimeter.shape"), this.leftPos + xPadding, this.topPos + yOff, CommonColors.WHITE);
        yOff += 16 + 20;
        guiGraphics.text(this.font, Component.translatable("mobbattle.gui.perimeter.inner"), this.leftPos + xPadding, this.topPos + yOff, CommonColors.WHITE);
        yOff += 16 + 20;
        guiGraphics.text(this.font, Component.translatable("mobbattle.gui.perimeter.outer"), this.leftPos + xPadding, this.topPos + yOff, CommonColors.WHITE);
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
    }

    protected void buttons() {
        int padding = 16;
        int yOff = padding + 12;
        this.addRenderableWidget(CycleButton.builder(shape -> Component.translatable(shape.translationKey), this.perimeter.shape())
                .withValues(PerimeterData.Shape.values())
                .displayState(CycleButton.DisplayState.VALUE)
                .create(this.leftPos + this.sizeX / 2 - 50, this.topPos + yOff, 100, 20, Component.empty(), (b, s) -> {
                    PerimeterSettingScreen.this.updateInfo(p -> p.withShape(s));
                }));
        yOff += 16 + 20;
        EditBox innerBox = new EditBox(this.font, this.leftPos + padding, this.topPos + yOff, 36, 10, Component.empty()) {
            @Override
            public boolean charTyped(CharacterEvent event) {
                if (Character.isDigit(event.codepoint()) || PerimeterSettingScreen.this.isHelperKey(event.codepoint())) {
                    if (super.charTyped(event) && !this.getValue().isEmpty()) {
                        try {
                            int num = Integer.parseInt(this.getValue());
                            if (num < 1) {
                                num = 1;
                                this.setValue(num + "");
                            }
                            int finalNum = num;
                            PerimeterSettingScreen.this.updateInfo(p -> p.withInner(finalNum));
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        innerBox.setMaxLength(3);
        innerBox.setEditable(true);
        innerBox.setValue(this.perimeter.inner() + "");
        this.addRenderableWidget(innerBox);

        yOff += 16 + 20;
        this.outerBox = new EditBox(this.font, this.leftPos + padding, this.topPos + yOff, 36, 10, Component.empty()) {
            @Override
            public boolean charTyped(CharacterEvent event) {
                if (Character.isDigit(event.codepoint()) || PerimeterSettingScreen.this.isHelperKey(event.codepoint())) {
                    if (super.charTyped(event) && !this.getValue().isEmpty()) {
                        try {
                            int num = Integer.parseInt(this.getValue());
                            if (num < 1) {
                                num = 1;
                                this.setValue(num + "");
                            }
                            int finalNum = num;
                            PerimeterSettingScreen.this.updateInfo(p -> p.withOuter(finalNum));
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);
                if (!focused) {
                    this.setValue(PerimeterSettingScreen.this.perimeter.outer() + "");
                }
            }
        };
        this.outerBox.setMaxLength(2);
        this.outerBox.setEditable(true);
        this.outerBox.setValue(this.perimeter.outer() + "");
        this.addRenderableWidget(this.outerBox);
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    private void updateInfo(Function<PerimeterComponent, PerimeterComponent> perimeter) {
        PerimeterComponent newComp = perimeter.apply(this.perimeter);
        if (!newComp.equals(this.perimeter)) {
            this.perimeter = newComp;
            if (!this.outerBox.isFocused())
                this.outerBox.setValue(this.perimeter.outer() + "");
            CrossPlatformStuff.INSTANCE.sendToServer(new C2SPerimeterComponent(this.hand, this.perimeter));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.minecraft.options.keyInventory.matches(event)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean click = super.mouseClicked(event, doubleClick);
        if (!click)
            this.setFocused(null);
        return click;
    }
}
