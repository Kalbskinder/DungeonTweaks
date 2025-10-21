package net.kalbskinder.dungeons.boss;

import net.kalbskinder.utils.MessageUtils;
import net.kalbskinder.utils.NameUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

public class AnnounceSpiritBear {

    private static final MessageUtils messageUtils = new MessageUtils();
    private static final NameUtils nameUtils = new NameUtils();

    public static void announce(String message) {
        if (message == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
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
