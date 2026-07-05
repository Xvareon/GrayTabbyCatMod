package github.xvareon.graytabbycatmod.config;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ModConfigHandler {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableGrayTabbyCatAttack;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            enableGrayTabbyCatAttack = builder
                    .comment("Enable or disable Gray Tabby cat's attack goal [Default: false]")
                    .define("enableGrayTabbyCatAttack", false);

            builder.pop();
        }
    }

    // Called on config load
    public static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            //
        }
    }

    // Called on config reload
    public static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            //
        }
    }

    /**
     * Safe getter for config values that defaults to the provided fallback
     * if the config is not yet loaded, has an invalid value, or any error occurs.
     * Catches:
     * - IllegalStateException: config not loaded yet (dev environment)
     * - ClassCastException: user entered wrong type (e.g., string instead of boolean)
     * - Any other exception as a defensive measure
     * @param configValue the ForgeConfigSpec.BooleanValue to read
     * @param defaultValue the fallback value if config is unavailable or invalid
     * @return the config value if available and valid, otherwise defaultValue
     */
    public static boolean safeGetBoolean(ForgeConfigSpec.BooleanValue configValue, boolean defaultValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException ex) {
            // Config not loaded yet in dev environment
            return defaultValue;
        } catch (ClassCastException ex) {
            // User entered a non-boolean value (e.g., "yes" instead of true)
            return defaultValue;
        } catch (Exception ex) {
            // Defensive: any other unexpected error, use default
            return defaultValue;
        }
    }
}