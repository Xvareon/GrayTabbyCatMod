package github.xvareon.graytabbycatmod.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DoggoVariant implements StringRepresentable {
    OGGIE(0, "oggie"),
    PEANUT(1, "peanut"),
    NUGGETS(2, "nuggets"),
    BEAGLE(3, "beagle"),
    BLACK_MOUNTAIN_FEIST(4, "black_mountain_feist"),
    BLOCCO(5, "blocco"),
    BORDER_COLLIE(6, "border_collie"),
    BOSTON_TERRIER(7, "boston_terrier"),
    GOLDEN_RETRIEVER(8, "golden_retriever"),
    PUG(9, "pug"),
    RED_SHIBA_INU(10, "red_shiba_inu"),
    SIBERIAN_HUSKY(11, "siberian_husky"),
    ST_BERNARD(12, "st_bernard"),
    WHITE_TERRIER(13, "white_terrier");

    private static final DoggoVariant[] BY_ID = Arrays.stream(values())
            .sorted((a, b) -> Integer.compare(a.id, b.id))
            .toArray(DoggoVariant[]::new);

    private static final Map<String, DoggoVariant> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(DoggoVariant::getName, Function.identity()));

    private final int id;
    private final String name;

    DoggoVariant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static DoggoVariant byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : OGGIE;
    }

    public static DoggoVariant byName(String name) {
        return BY_NAME.getOrDefault(name.toLowerCase(), OGGIE);
    }

    public static DoggoVariant getRandomVariant(RandomSource random) {
        return values()[random.nextInt(values().length)];
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return name;
    }

    // Breed-specific default size multipliers
    public float getDefaultSize() {
        return switch (this) {
            case PUG, PEANUT, WHITE_TERRIER, NUGGETS -> 0.7f;   // small breeds
            case BEAGLE, BOSTON_TERRIER, RED_SHIBA_INU -> 0.85f; // medium-small
            case BORDER_COLLIE, BLOCCO -> 1.0f; // medium
            case GOLDEN_RETRIEVER, SIBERIAN_HUSKY -> 1.15f; // large
            case ST_BERNARD, OGGIE -> 1.25f; // giant breeds
            case BLACK_MOUNTAIN_FEIST -> 0.9f; // mid-sized
        };
    }
}
