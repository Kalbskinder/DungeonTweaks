package net.kalbskinder.utils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class MessageUtils {
    private final static Text PREFIX = Text.literal("[")
            .append(Text.literal("Dungeon").withColor(0xd40000))
            .append(Text.literal("Tweaks").withColor(0xff4747))
            .append(Text.literal("] "));

    public void sendMessage(ClientPlayerEntity player, String message, boolean withPrefix) {
        if (withPrefix) {
            player.sendMessage(PREFIX.copy().append(Text.literal(message)), false);
        } else {
            player.sendMessage(Text.literal(message), false);
        }
    }
}
