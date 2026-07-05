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
}