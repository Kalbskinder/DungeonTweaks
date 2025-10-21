package net.kalbskinder.dungeons.boss;

import net.kalbskinder.utils.MessageUtils;
import net.kalbskinder.utils.NameUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

// Logging
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnnounceSpiritBear {

    private static final MessageUtils messageUtils = new MessageUtils();
    private static final NameUtils nameUtils = new NameUtils();
    private static final Logger LOGGER = LogManager.getLogger("dungeontweaks");

    public static void announce(String message) {
        if (message == null) return;
        LOGGER.info("Server message received: {}", message);

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            String plainDebug = nameUtils.stripColorCodes(message).trim();
            // always show an in-game debug copy so the user can see raw messages
            messageUtils.sendMessage(client.player, "[DT DEBUG] Server: " + plainDebug, true);
        }

        String plain = nameUtils.stripColorCodes(message).trim();
        if (plain.isEmpty()) return;

        if (!plain.contains("A Spirit Bear has appeared!")) return;
        if (client == null || client.player == null) return;

        client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0F, 1.0F);
        messageUtils.sendMessage(client.player, "§a§lSpirit Bear spawned!", true);

        // Trigger HUD title display via the tracker for 5 seconds
        SpiritBearTracker.INSTANCE.triggerTitle("Spirit Bear spawned!", 20 * 5);
    }
}
