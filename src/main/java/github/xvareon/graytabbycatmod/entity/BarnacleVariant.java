package github.xvareon.graytabbycatmod.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum BarnacleVariant implements StringRepresentable {
    DEFAULT(0, "default"),
    BLUE(1, "blue"),
    RED(2, "red"),
    YELLOW(3, "yellow"),
    WHITE(4, "white"),
    BLACK(5, "black");

    private static final BarnacleVariant[] BY_ID = Arrays.stream(values()).sorted((a, b) -> Integer.compare(a.id, b.id)).toArray(BarnacleVariant[]::new);
    private static final Map<String, BarnacleVariant> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(BarnacleVariant::getName, Function.identity()));
    private final int id;
    private final String name;

    BarnacleVariant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static BarnacleVariant byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : DEFAULT;
    }

    public static BarnacleVariant byName(String name) {
        return BY_NAME.getOrDefault(name.toLowerCase(), DEFAULT);
    }

    public static BarnacleVariant getRandomVariant(RandomSource random) {
        return values()[random.nextInt(values().length)];
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return name;
    }
}
