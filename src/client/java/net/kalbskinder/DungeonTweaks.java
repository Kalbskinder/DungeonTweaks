package net.kalbskinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.kalbskinder.dungeons.boss.SpiritBearTracker;
import net.kalbskinder.render.HudRenderer;
import net.minecraft.entity.LivingEntity;

public class DungeonTweaks implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Tick handler to scan nearby entities (kept for legacy detection)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;
            client.world.getEntitiesByClass(LivingEntity.class, client.player.getBoundingBox().expand(100), entity -> true)
                    .forEach(entity -> SpiritBearTracker.INSTANCE.onEntityDetected(entity, client.world));
            SpiritBearTracker.INSTANCE.clientTick();
        });

        // HUD render callback
        HudRenderCallback.EVENT.register(HudRenderer::onHudRender);

        // Listen for server game messages and forward them to the tracker
        ServerMessageEvents.GAME_MESSAGE.register((client, message, overlay) -> {
            SpiritBearTracker.INSTANCE.onChatMessage(message.getString());
        });
    }
}