package github.xvareon.graytabbycatmod.init;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GrayTabbyCatMod.MODID);

    public static final RegistryObject<EntityType<GrayTabbyCat>> GRAY_TABBY_CAT = ENTITIES.register("gray_tabby_cat",
            () -> EntityType.Builder.<GrayTabbyCat>of(GrayTabbyCat::new, MobCategory.CREATURE)
                    .sized(1.0f, 1.0f)
                    .build(new ResourceLocation(GrayTabbyCatMod.MODID, "gray_tabby_cat").toString())
    );

    public static final RegistryObject<EntityType<DragonFireball>> DRAGON_FIREBALL = ENTITIES.register("dragon_fireball",
            () -> EntityType.Builder.<DragonFireball>of(DragonFireball::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(new ResourceLocation(GrayTabbyCatMod.MODID, "dragon_fireball").toString())
    );

    public static final RegistryObject<EntityType<LightningCharge>> LIGHTNING_CHARGE = ENTITIES.register("lightning_charge",
            () -> EntityType.Builder.<LightningCharge>of(LightningCharge::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(new ResourceLocation(GrayTabbyCatMod.MODID, "lightning_charge").toString())
    );

    public static final RegistryObject<EntityType<Barnacle>> BARNACLE = ENTITIES.register("barnacle",
            () -> EntityType.Builder.<Barnacle>of(Barnacle::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.0f)
                    .build(new ResourceLocation(GrayTabbyCatMod.MODID, "barnacle").toString())
    );

    public static final RegistryObject<EntityType<TamableOcelot>> TAMABLE_OCELOT = ENTITIES.register("tamable_ocelot",
            () -> EntityType.Builder.<TamableOcelot>of(TamableOcelot::new, MobCategory.CREATURE)
                    .sized(1.0f, 1.0f)
                    .build(new ResourceLocation(GrayTabbyCatMod.MODID, "tamable_ocelot").toString())
    );
}