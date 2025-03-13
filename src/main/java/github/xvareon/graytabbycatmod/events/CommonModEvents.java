package github.xvareon.graytabbycatmod.events;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.entity.Barnacle;
import github.xvareon.graytabbycatmod.entity.GrayTabbyCat;
import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GrayTabbyCatMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class CommonModEvents {

    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event){
        event.put(EntityInit.GRAY_TABBY_CAT.get(), GrayTabbyCat.createAttributes().build());
        event.put(EntityInit.BARNACLE.get(), Barnacle.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event){
        event.register(
                EntityInit.GRAY_TABBY_CAT.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.WORLD_SURFACE,
                GrayTabbyCat::canSpawn,
                SpawnPlacementRegisterEvent.Operation.OR
        );
        event.register(
                EntityInit.BARNACLE.get(),
                SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Barnacle::canSpawn,
                SpawnPlacementRegisterEvent.Operation.OR
        );
    }
}
