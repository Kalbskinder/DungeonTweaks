package net.kalbskinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.kalbskinder.dungeons.boss.AnnounceSpiritBear;
import net.kalbskinder.dungeons.boss.SpiritBearTracker;
import net.kalbskinder.render.HudRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

// Logging
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonTweaks implements ClientModInitializer {

    private static final Logger LOGGER = LogManager.getLogger("dungeontweaks");
    private static KeyBinding debugTriggerKey;

    @Override
    public void onInitializeClient() {

        // HUD render callback
        HudRenderCallback.EVENT.register(HudRenderer::onHudRender);

        // Listen for server game messages and forward them to the tracker
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            AnnounceSpiritBear.announce(message.getString());
        });

        // Client tick: update trackers (reduces title timer) and check debug key
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            SpiritBearTracker.INSTANCE.clientTick();
            if (client.world != null && client.world.getTime() % 20L == 0L) {
            }
        });
    }
}