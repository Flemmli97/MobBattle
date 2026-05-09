package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.gui.widget.SuggestionEditBox;
import io.github.flemmli97.mobbattle.common.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.common.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.network.C2SSpawnEgg;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

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
        SpawnEggOptions options = stack.getOrDefault(MobBattleDataComponents.SPAWN_EGG_OPTIONS.get(), SpawnEggOptions.DEFAULT);
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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(this.leftPos, this.topPos, this.leftPos + this.sizeX, this.topPos + this.sizeY, 0xc0101010, 0xc0101010);
        int xPadding = 16;
        int yOff = xPadding;
        int width = this.font.width(this.entity.getType().getDescription());
        guiGraphics.text(this.font, this.entity.getType().getDescription(), (int) (this.leftPos + this.sizeX * 0.5f - width * 0.5f), this.topPos + yOff, ARGB.color(255, ChatFormatting.GOLD.getColor()));
        yOff += 16;
        guiGraphics.text(this.font, Component.translatable("mobbattle.gui.team"), this.leftPos + xPadding, this.topPos + yOff, CommonColors.WHITE);
        yOff += 16 + 20 + 8;
        guiGraphics.text(this.font, Component.translatable("mobbattle.gui.amount"), this.leftPos + xPadding, this.topPos + yOff, CommonColors.WHITE);
        yOff += 16 + 20;
        guiGraphics.text(this.font, Component.translatable("mobbattle.gui.spacing"), this.leftPos + xPadding, this.topPos + yOff, CommonColors.WHITE);
        extractEntityInInventoryFollowsMouse(guiGraphics,
                this.leftPos + this.sizeX - xPadding - (3 * 30), this.topPos + xPadding + 16, 30, 3f, 3,
                0.0625f, mouseX, mouseY, this.entity);
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static void extractEntityInInventoryFollowsMouse(GuiGraphicsExtractor graphics, int x, int y, int scale, float maxWidth, float maxHeight,
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
        InventoryScreen.extractEntityInInventoryFollowsMouse(graphics,
                x, y, x + sizeX, y + sizeY,
                (int) (scale * scaleMult), yOffset, mouseX, mouseY, entity);
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
    }

    protected void buttons() {
        int padding = 16;
        int yOff = padding + 12 + 16;
        this.teamBox = new SuggestionEditBox(this.font, this.leftPos + padding, this.topPos + yOff, 100, 14, Component.empty(), 5, false,
                SuggestionEditBox.ofString(this.player.level().getScoreboard().getTeamNames())) {
            @Override
            public boolean charTyped(CharacterEvent event) {
                if (event.codepoint() == GLFW.GLFW_KEY_SPACE)
                    return false;
                return super.charTyped(event);
            }
        };
        this.teamBox.setResponder(s -> this.team = s);
        this.teamBox.setMaxLength(35);
        this.teamBox.setEditable(true);
        this.teamBox.setValue(this.team);
        this.addRenderableWidget(this.teamBox);

        yOff += 16 + 20 + 8;
        EditBox amountBox = new EditBox(this.font, this.leftPos + padding, this.topPos + yOff, 27, 10, Component.empty()) {
            @Override
            public boolean charTyped(CharacterEvent event) {
                if (Character.isDigit(event.codepoint()) || SpawnEggScreen.this.isHelperKey(event.codepoint())) {
                    if (super.charTyped(event) && !this.getValue().isEmpty()) {
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
            public boolean charTyped(CharacterEvent event) {
                if (Character.isDigit(event.codepoint()) || SpawnEggScreen.this.isHelperKey(event.codepoint())) {
                    if (super.charTyped(event) && !this.getValue().isEmpty()) {
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
    public boolean keyPressed(KeyEvent event) {
        if (!this.teamBox.canConsumeInput() && this.minecraft.options.keyInventory.matches(event)) {
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
