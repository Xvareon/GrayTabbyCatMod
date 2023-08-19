package github.xvareon.graytabbycatmod.init;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.GrayTabbyCat;
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
}