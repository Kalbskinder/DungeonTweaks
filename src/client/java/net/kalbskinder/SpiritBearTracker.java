package net.kalbskinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Client-side tracker for Spirit Bear entities.
 * - detects entities via client tick and chunk load
 * - tracks a WeakReference per UUID, an alerted flag, and a titleTicks TTL
 */
public class SpiritBearTracker {

    public static final SpiritBearTracker INSTANCE = new SpiritBearTracker();
    private static final Logger logger = Logger.getLogger("dungeontweaks");

    public static class Tracked {
        public final WeakReference<Entity> ref;
        public final boolean fromChat;
        public boolean alerted;
        public int titleTicks; // ticks remaining to show HUD title

        // Entity-based tracked
        public Tracked(Entity e, int titleTicks) {
            this.ref = new WeakReference<>(e);
            this.fromChat = false;
            this.alerted = false;
            this.titleTicks = titleTicks;
        }

        // Chat-based tracked (no entity reference)
        public Tracked(int titleTicks) {
            this.ref = new WeakReference<>(null);
            this.fromChat = true;
            this.alerted = false;
            this.titleTicks = titleTicks;
        }
    }

    // tracked map
    private final Map<UUID, Tracked> tracked = new ConcurrentHashMap<>();

    // Fixed UUID for chat-based entries (so repeated chat messages refresh the same entry)
    private static final UUID CHAT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    // duration for HUD title display (ticks, 20 ticks = 1s)
    private static final int TITLE_DURATION_TICKS = 20 * 5; // 5 seconds default

    // Set for known entity UUIDs
    private final Set<UUID> knownEntities = new HashSet<>();

    private SpiritBearTracker() {}

    // Called from client tick or chunk load (legacy usage)
    public void onEntityDetected(Entity entity, ClientWorld world) {
        if (entity == null || world == null) return;

        String raw = entity.getCustomName() != null ? entity.getCustomName().getString() : entity.getName().getString();
        String plain = NameUtils.stripColorCodes(raw).trim();

        logger.info("Plain text name detected: " + plain);

        if (!plain.contains("Spirit Bear")) return;

        UUID id = entity.getUuid();
        if (knownEntities.add(id)) {
            Tracked t = new Tracked(entity, TITLE_DURATION_TICKS);
            tracked.put(id, t);
            doAlert(t);
        }
    }

    public void onChatMessage(String rawMessage) {
        if (rawMessage == null) return;
        String plain = NameUtils.stripColorCodes(rawMessage).trim();

        if (!plain.contains("A Spirit Bear has appeared!")) return;

        Tracked existing = tracked.get(CHAT_UUID);
        if (existing != null && existing.fromChat) {
            existing.titleTicks = TITLE_DURATION_TICKS;
            if (!existing.alerted) {
                existing.alerted = true;
                doAlert(existing);
            }
            return;
        }

        Tracked t = new Tracked(TITLE_DURATION_TICKS);
        t.alerted = true;
        tracked.put(CHAT_UUID, t);
        doAlert(t);
    }

    // Play sound and send a short chat message. Called for both entity- and chat-based events.
    private void doAlert(Tracked t) {
        if (!t.alerted) t.alerted = true;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0F, 1.0F);
            client.player.sendMessage(Text.of("Spirit Bear spawned!"), false);
        }
    }

    // Housekeeping each client tick: remove dead entities and decrease title timers
    public void clientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Iterator<Map.Entry<UUID, Tracked>> it = tracked.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Tracked> entry = it.next();
            UUID id = entry.getKey();
            Tracked t = entry.getValue();
            if (t == null) {
                it.remove();
                continue;
            }

            if (t.fromChat) {
                if (t.titleTicks > 0) {
                    t.titleTicks--;
                } else {
                    it.remove();
                }
                continue;
            }

            // Entity-based logic
            Entity e = t.ref.get();
            if (e == null || !e.isAlive()) {
                it.remove();
                knownEntities.remove(id);
                continue;
            }
            if (t.titleTicks > 0) t.titleTicks--;
        }
    }

    @SuppressWarnings("unused")
    public Map<UUID, Tracked> getTrackedMap() {
        return tracked;
    }

    public boolean hasActiveTitle() {
        return tracked.values().stream().anyMatch(t -> t.titleTicks > 0);
    }

    public Text getTitleText() {
        return Text.of("Spirit Bear spawned!");
    }
}