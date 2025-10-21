package net.kalbskinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class HudRenderer {

    public static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        // touch tickCounter to avoid unused-parameter warnings in some IDEs
        tickCounter.hashCode();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        SpiritBearTracker tracker = SpiritBearTracker.INSTANCE;
        if (!tracker.hasActiveTitle()) return;

        Text title = tracker.getTitleText();
        TextRenderer font = client.textRenderer;

        int width = client.getWindow().getScaledWidth();
        int y = 10;
        int textWidth = font.getWidth(title);
        int x = (width - textWidth) / 2;

        int padding = 6;
        int bx1 = x - padding;
        int by1 = y - padding;
        int bx2 = x + textWidth + padding;
        int by2 = y + font.fontHeight + padding;

        // Draw semi-transparent black background and greenish title text
        drawContext.fill(bx1, by1, bx2, by2, 0x80000000);
        drawContext.drawTextWithShadow(font, title, x, y, 0x00FF66);
    }
}