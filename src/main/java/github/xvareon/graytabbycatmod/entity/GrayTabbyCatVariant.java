package github.xvareon.graytabbycatmod.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum GrayTabbyCatVariant implements StringRepresentable {
    DEFAULT(0, "default"),
    CALICO(1, "calico"),
    GREEN(2, "green"),
    YELLOW(3, "yellow"),
    ORANGE(4, "orange"),
    HALF(5, "half"),
    CYAN(6, "cyan"),
    BROWN(7, "brown"),
    PINK(8, "pink"),
    BLACK(9, "black"),
    RED(10, "red"),
    WHITE_CALICO(11, "white_calico"),
    WHITE_GREEN(12, "white_green"),
    WHITE_BLUE(13, "white_blue"),
    WHITE_YELLOW(14, "white_yellow"),
    WHITE_ORANGE(15, "white_orange"),
    WHITE_CYAN(16, "white_cyan"),
    WHITE_PINK(17, "white_pink"),
    GRAY(18, "gray"),
    VIOLET(19, "violet"),
    GREY(20, "grey");

    private static final GrayTabbyCatVariant[] BY_ID = Arrays.stream(values()).sorted((a, b) -> Integer.compare(a.id, b.id)).toArray(GrayTabbyCatVariant[]::new);
    private static final Map<String, GrayTabbyCatVariant> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(GrayTabbyCatVariant::getName, Function.identity()));
    private final int id;
    private final String name;

    GrayTabbyCatVariant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static GrayTabbyCatVariant byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : DEFAULT;
    }

    public static GrayTabbyCatVariant byName(String name) {
        return BY_NAME.getOrDefault(name.toLowerCase(), DEFAULT);
    }

    public static GrayTabbyCatVariant getRandomVariant(RandomSource random) {
        return values()[random.nextInt(values().length)];
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return name;
    }
}
