package net.kalbskinder.dungeons.boss;

import net.minecraft.text.Text;

// Logging
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Minimal client-side tracker for displaying a HUD title when a Spirit Bear spawns.
 *
 * Usage:
 * - Call SpiritBearTracker.INSTANCE.triggerTitle(Text.of("...") or Text.literal(...), durationTicks)
 * - Register clientTick() to be called every client tick to reduce the timer (done in DungeonTweaks)
 * - HudRenderer reads hasActiveTitle() and getTitleText()
 */
public class SpiritBearTracker {

    public static final SpiritBearTracker INSTANCE = new SpiritBearTracker();

    private static final Logger LOGGER = LogManager.getLogger("dungeontweaks");

    private Text titleText = null;
    private int titleTicks = 0; // remaining ticks to show the title (20 ticks = 1s)

    private SpiritBearTracker() {}

    /** Trigger showing a HUD title for the given duration (in ticks). */
    public void triggerTitle(Text text, int durationTicks) {
        if (text == null || durationTicks <= 0) return;
        this.titleText = text;
        this.titleTicks = durationTicks;
        // Debug: log when a title is triggered
    }

    /** Convenience overload using literal string. */
    public void triggerTitle(String text, int durationTicks) {
        triggerTitle(Text.literal(text), durationTicks);
    }

    /** Called every client tick to decrease the remaining title timer. */
    public void clientTick() {
        if (titleTicks > 0) {
            titleTicks--;
            // Debug: print when timer reaches zero
            if (titleTicks == 0) {
                LOGGER.debug("titleTicks expired, clearing title");
                titleText = null;
            }
        }
    }

    public boolean hasActiveTitle() {
        return titleTicks > 0 && titleText != null;
    }

    public Text getTitleText() {
        return titleText != null ? titleText : Text.empty();
    }
}
