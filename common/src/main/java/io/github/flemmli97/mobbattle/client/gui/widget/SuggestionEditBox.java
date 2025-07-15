package io.github.flemmli97.mobbattle.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.function.Consumer;

public class SuggestionEditBox extends EditBox {

    private final Font font;
    private final int limit, lineHeight;
    private final Collection<SuggestionContent> allSuggestions;
    private final boolean top;

    private int offset;
    private int current, hovered;
    private String[] suggestions;
    private Rect2i rect;
    private boolean hidden;

    private boolean init;

    private int paddingX = 4, paddingY = 2;

    public SuggestionEditBox(Font font, int x, int y, int width, int height, Component message,
                             int maxLimit, boolean top, Collection<SuggestionContent> suggestions) {
        super(font, x, y, width, height, message);
        this.font = font;
        this.allSuggestions = suggestions;
        this.limit = maxLimit;
        this.lineHeight = this.font.lineHeight + 3;
        this.top = top;
        this.recalculateSuggestions("");

        this.select(0);
        this.setResponder(null);
    }

    public static Collection<SuggestionContent> ofString(Collection<String> strings) {
        return strings.stream().sorted().<SuggestionContent>map(s -> new SuggestionContent() {

            @Override
            public boolean matches(String input) {
                return s.startsWith(input);
            }

            @Override
            public String asString() {
                return s;
            }
        }).toList();
    }

    public static Collection<SuggestionContent> ofResourceLocation(Collection<ResourceLocation> strings) {
        return strings.stream().sorted((r1, r2) -> {
            if (r1.getNamespace().equals("minecraft")) {
                if (r2.getNamespace().equals("minecraft"))
                    return r1.getPath().compareTo(r2.getPath());
                return -1;
            }
            return r1.toString().compareTo(r2.toString());
        }).<SuggestionContent>map(res -> new SuggestionContent() {

            @Override
            public boolean matches(String input) {
                return res.getPath().startsWith(input) || res.toString().startsWith(input);
            }

            @Override
            public String asString() {
                return res.toString();
            }
        }).toList();
    }

    public SuggestionEditBox withPadding(int paddingX, int paddingY) {
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.suggestionsHidden() || this.suggestions.length == 0)
            return;
        if (this.suggestions.length == 1 && this.getValue().equals(this.suggestions[0]))
            return;
        int idx = this.indexFromMouse(mouseY);
        if (idx >= 0 && idx < this.suggestions.length) {
            this.select(idx);
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 1);
        guiGraphics.fill(this.rect.getX(), this.rect.getY(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight(), 0xe0101010);
        int x = this.getX() + this.paddingX;
        int y = this.rect.getY() + this.paddingY;
        for (int i = 0; i < this.suggestions.length; i++) {
            int idxx = (this.offset + i) % this.suggestions.length;
            if (i >= 5 || idxx >= this.suggestions.length)
                break;
            String string = this.suggestions[idxx];
            guiGraphics.drawString(this.font, string, x, y + i * this.lineHeight, this.current == idxx ? 0xFFFF55 : 0xFFFFFF);
        }
        guiGraphics.pose().popPose();
    }

    private boolean suggestionsHidden() {
        return this.hidden || !this.canConsumeInput();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean suggestion = !this.suggestionsHidden() && this.rect.contains((int) mouseX, (int) mouseY);
        if (!suggestion && super.mouseClicked(mouseX, mouseY, button)) {
            this.hidden = false;
            return true;
        }
        if (!suggestion) {
            return false;
        }
        int i = this.indexFromMouse(mouseY);
        if (i >= 0 && i < this.suggestions.length) {
            this.select(i);
            this.useSuggestion();
        }
        return true;
    }

    private int indexFromMouse(double mouseY) {
        return (int) ((mouseY - (this.rect.getY() - this.paddingY)) / this.lineHeight + this.offset);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.rect.contains((int) mouseX, (int) mouseY)) {
            this.offset = Mth.clamp((int) (this.offset - scrollY), 0, Math.max(this.suggestions.length - this.limit, 0));
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.suggestionsHidden() && this.rect.contains((int) mouseX, (int) mouseY)) {
            return true;
        }
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.canConsumeInput()) {
            if (keyCode == GLFW.GLFW_KEY_UP) {
                this.cycle(-1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                this.cycle(1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                this.useSuggestion();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setResponder(Consumer<String> responder) {
        super.setResponder(this.of(responder));
    }

    private Consumer<String> of(Consumer<String> other) {
        Consumer<String> updater = s -> {
            String val = this.getValue();
            this.recalculateSuggestions(val);
        };
        if (other == null || !this.init) {
            this.init = true;
            return updater;
        }
        return s -> {
            other.accept(s);
            updater.accept(s);
        };
    }

    private void recalculateSuggestions(String input) {
        this.suggestions = this.allSuggestions.stream().filter(ctx -> ctx.matches(input))
                .map(SuggestionContent::asString).toArray(String[]::new);
        int sizeY = Math.min(this.suggestions.length, this.limit) * this.lineHeight;
        int y = this.getY() + (this.top ? -sizeY - this.paddingY : this.getHeight() + this.paddingY);
        int width = this.width;
        for (String sugg : this.suggestions) {
            int newWidth = this.font.width(sugg) + this.paddingX * 2;
            if (newWidth > width)
                width = newWidth;
        }
        this.rect = new Rect2i(this.getX(), y, width, sizeY + this.paddingY);
        this.hidden = false;
    }

    public void cycle(int change) {
        this.select(this.current + change);
        int i = this.offset;
        int j = this.offset + this.limit - 1;
        if (this.current < i) {
            this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestions.length - this.limit, 0));
        } else if (this.current > j) {
            this.offset = Mth.clamp(this.current - this.limit, 0, Math.max(this.suggestions.length - this.limit, 0));
        }
    }

    public void select(int index) {
        this.current = index;
        if (this.current < 0) {
            this.current += this.suggestions.length;
        }
        if (this.current >= this.suggestions.length) {
            this.current -= this.suggestions.length;
        }
    }

    public void useSuggestion() {
        String suggestion = this.suggestions[this.current];
        this.setValue(suggestion);
        this.setCursorPosition(suggestion.length());
        this.setHighlightPos(suggestion.length());
        this.select(this.current);
        this.hidden = true;
    }

    public interface SuggestionContent {

        boolean matches(String input);

        String asString();
    }
}
