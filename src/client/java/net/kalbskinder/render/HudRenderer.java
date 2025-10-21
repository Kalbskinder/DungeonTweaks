package net.kalbskinder.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

// Logging
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HudRenderer {

    private static final Logger LOGGER = LogManager.getLogger("dungeontweaks");

    public static void onHudRender(DrawContext drawContext, @SuppressWarnings("unused") RenderTickCounter tickCounter) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        net.kalbskinder.dungeons.boss.SpiritBearTracker tracker = net.kalbskinder.dungeons.boss.SpiritBearTracker.INSTANCE;

        // Sparse debug: once per second, print that the HUD render callback ran and whether a title is active
        if (client.world != null && client.world.getTime() % 20L == 0L) {
            LOGGER.debug("HudRenderer.onHudRender: activeTitle={}", tracker.hasActiveTitle());
        }

        if (!tracker.hasActiveTitle()) return;

        Text title = tracker.getTitleText();
        // Use the new helper to render the title (keeps existing visuals)
        renderHudText(drawContext, title, 0x00FF66);
    }

    /**
     * Render a HUD text element from a raw String with the given color.
     * This is a convenience wrapper that converts the String to a Text and calls
     * the Text-based method.
     *
     * Usage example:
     * HudRenderer.renderHudText(drawContext, "Hello world", 0xFF0000);
     *
     * Note: color is an int RGB value (0xRRGGBB). Alpha is ignored by the
     * DrawContext text renderer.
     */
    @SuppressWarnings("unused")
    public static void renderHudText(DrawContext drawContext, String text, int color) {
        renderHudText(drawContext, Text.literal(text), color);
    }

    /**
     * Render a HUD text element from a Text object with the given color.
     * Centers the text horizontally and draws a semi-transparent background box.
     * The text is drawn scaled (larger) and the box adapts to the scaled text size.
     */
    public static void renderHudText(DrawContext drawContext, Text text, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || text == null) return;

        TextRenderer font = client.textRenderer;

        int width = client.getWindow().getScaledWidth();
        int yVis = 24; // Distance from top of screen

        float scale = 1.35f; // Font scale factor

        int textWidth = font.getWidth(text);
        int textHeight = font.fontHeight;

        // Scaled visual sizes
        int scaledTextWidth = Math.round(textWidth * scale);
        int scaledTextHeight = Math.round(textHeight * scale);

        int xVis = (width - scaledTextWidth) / 2;

        int padding = 8; // padding around text in box
        int bx1 = xVis - padding;
        int by1 = yVis - padding;
        int bx2 = xVis + scaledTextWidth + padding;
        int by2 = yVis + scaledTextHeight + padding;

        // Background box
        drawContext.fill(bx1, by1, bx2, by2, 0x80000000);

        // Draw scaled text: scale the matrix, but pass coordinates adjusted for scale
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(scale, scale, scale);
        int xDraw = Math.round(xVis / scale);
        int yDraw = Math.round(yVis / scale);
        drawContext.drawTextWithShadow(font, text, xDraw, yDraw, color);
        drawContext.getMatrices().pop();
    }
}