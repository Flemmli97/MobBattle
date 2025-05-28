package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.mobbattle.network.C2SSpawnEgg;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpawnEggScreen extends Screen {

    private final Player player;
    protected final Mob entity;
    private final InteractionHand hand;

    private int leftPos, topPos;
    private final int sizeX = 240;
    private final int sizeY = 200;

    private String team;
    private int amount, spacing;
    private EditBox teamBox;

    public SpawnEggScreen(InteractionHand hand) {
        super(Component.empty());
        this.hand = hand;
        this.player = Minecraft.getInstance().player;
        ItemStack stack = this.player.getItemInHand(this.hand);
        Entity entity = ItemExtendedSpawnEgg.getEntity(this.player.level(), stack);
        this.entity = entity instanceof Mob mob ? mob : null;
        SpawnEggOptions options = stack.getOrDefault(CrossPlatformStuff.INSTANCE.getComponentSpawnEggOptions(), SpawnEggOptions.DEFAULT);
        this.team = options.team() != null ? options.team() : "";
        this.amount = options.amount();
        this.spacing = options.spacing();
    }

    @Override
    protected void init() {
        super.init();
        if (this.entity == null) {
            this.minecraft.setScreen(null);
            return;
        }
        this.leftPos = this.width / 2 - (this.sizeX / 2);
        this.topPos = this.height / 2 - (this.sizeY / 2);
        this.buttons();
    }

    @Override
    public void tick() {
        super.tick();
        this.entity.tickCount++;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(this.leftPos, this.topPos, this.leftPos + this.sizeX, this.topPos + this.sizeY, 0xc0101010, 0xc0101010);
        int xPadding = 16;
        int yOff = xPadding;
        int width = this.font.width(this.entity.getType().getDescription());
        guiGraphics.drawString(this.font, this.entity.getType().getDescription(), (int) (this.leftPos + this.sizeX * 0.5f - width * 0.5f), this.topPos + yOff, ChatFormatting.GOLD.getColor());
        yOff += 16;
        guiGraphics.drawString(this.font, Component.translatable("mobbattle.gui.team"), this.leftPos + xPadding, this.topPos + yOff, 0xffffff);
        yOff += 16 + 20 + 8;
        guiGraphics.drawString(this.font, Component.translatable("mobbattle.gui.amount"), this.leftPos + xPadding, this.topPos + yOff, 0xffffff);
        yOff += 16 + 20;
        guiGraphics.drawString(this.font, Component.translatable("mobbattle.gui.spacing"), this.leftPos + xPadding, this.topPos + yOff, 0xffffff);

        renderEntityMouseNoClip(guiGraphics,
                this.leftPos + this.sizeX - xPadding - (3 * 30), this.topPos + xPadding + 16, 30, 3f, 3,
                0.0625f, mouseX, mouseY, this.entity);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static void renderEntityMouseNoClip(GuiGraphics guiGraphics, int x, int y, int scale, float maxWidth, float maxHeight,
                                               float yOffset, float mouseX, float mouseY, LivingEntity entity) {
        int sizeX = (int) (maxWidth * scale);
        int sizeY = (int) (maxHeight * scale);
        float scaleMult = 1;
        if (entity.getBbWidth() > maxWidth) {
            scaleMult = maxWidth / entity.getBbWidth();
        }
        if (entity.getBbHeight() > maxHeight) {
            scaleMult = Math.min(scaleMult, maxHeight / entity.getBbHeight());
        }
        renderEntityMouseNoClip(guiGraphics,
                x, y, x + sizeX, y + sizeY,
                (int) (scale * scaleMult), yOffset, mouseX, mouseY, entity);
    }

    private static void renderEntityMouseNoClip(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, float mouseX, float mouseY, LivingEntity entity) {
        float xM = (float) (x1 + x2) / 2.0f;
        float yM = (float) (y1 + y2) / 2.0f;
        float yRot = (float) Math.atan((xM - mouseX) / 40.0f);
        float xRot = (float) Math.atan((yM - mouseY) / 40.0f);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(xRot * 20.0f * ((float) Math.PI / 180));
        quaternionf.mul(quaternionf2);
        float j = entity.yBodyRot;
        float k = entity.getYRot();
        float l = entity.getXRot();
        float m = entity.yHeadRotO;
        float n = entity.yHeadRot;
        entity.yBodyRot = 180.0f + yRot * 20.0f;
        entity.setYRot(180.0f + yRot * 40.0f);
        entity.setXRot(-xRot * 20.0f);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        float o = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0f, entity.getBbHeight() / 2.0f + yOffset * o, 0.0f);
        float p = (float) scale / o;
        InventoryScreen.renderEntityInInventory(guiGraphics, xM, yM, p, vector3f, quaternionf, quaternionf2, entity);
        entity.yBodyRot = j;
        entity.setYRot(k);
        entity.setXRot(l);
        entity.yHeadRotO = m;
        entity.yHeadRot = n;
    }

    @Override
    protected void renderBlurredBackground() {
    }

    protected void buttons() {
        int padding = 16;
        int yOff = padding + 12 + 16;
        this.teamBox = new SuggestionEditBox(this.font, this.leftPos + padding, this.topPos + yOff, 100, 14, Component.empty(), 5, false,
                SuggestionEditBox.ofString(this.player.level().getScoreboard().getTeamNames()));
        this.teamBox.setResponder(s -> this.team = s);
        this.teamBox.setMaxLength(35);
        this.teamBox.setEditable(true);
        this.teamBox.setValue(this.team);
        this.addRenderableWidget(this.teamBox);

        yOff += 16 + 20 + 8;
        EditBox amountBox = new EditBox(this.font, this.leftPos + padding, this.topPos + yOff, 27, 10, Component.empty()) {
            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || SpawnEggScreen.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            int amount = Integer.parseInt(this.getValue());
                            if (amount > 100) {
                                amount = 100;
                                this.setValue(amount + "");
                            }
                            SpawnEggScreen.this.amount = amount;
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        amountBox.setMaxLength(3);
        amountBox.setEditable(true);
        amountBox.setValue(this.amount + "");
        this.addRenderableWidget(amountBox);

        yOff += 16 + 20;
        EditBox spacingBox = new EditBox(this.font, this.leftPos + padding, this.topPos + yOff, 27, 10, Component.empty()) {
            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || SpawnEggScreen.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            SpawnEggScreen.this.spacing = Integer.parseInt(this.getValue());
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        spacingBox.setMaxLength(2);
        spacingBox.setEditable(true);
        spacingBox.setValue(this.spacing + "");
        this.addRenderableWidget(spacingBox);

        yOff = this.sizeY - padding - 20;
        this.addRenderableWidget(Button.builder(Component.translatable("mobbattle.gui.save"), b -> {
            CrossPlatformStuff.INSTANCE.sendToServer(new C2SSpawnEgg(this.hand, this.team, this.amount, this.spacing));
            this.minecraft.setScreen(null);
        }).bounds(this.leftPos + this.sizeX / 2 - 50, this.topPos + yOff, 100, 20).build());
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.teamBox.canConsumeInput() && this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean click = super.mouseClicked(mouseX, mouseY, button);
        if (!click)
            this.setFocused(null);
        return click;
    }
}
