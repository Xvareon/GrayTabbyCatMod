package github.xvareon.graytabbycatmod.init;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import github.xvareon.graytabbycatmod.block.GlowingObsidianBlock;
import github.xvareon.graytabbycatmod.block.SoulSandGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GrayTabbyCatMod.MODID);

    public static final RegistryObject<Block> GLOWING_OBSIDIAN = BLOCKS.register("glowing_obsidian",
            () -> new GlowingObsidianBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)
                    .mapColor(MapColor.CRIMSON_HYPHAE)
                    .strength(50.0f, 1200.0f)
                    .instrument(NoteBlockInstrument.DRAGON)
                    .lightLevel(state -> 10)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.BLOCK)
                    .emissiveRendering((state, getter, pos) -> true)
            ));

    public static final RegistryObject<Block> SOUL_SAND_GLASS = BLOCKS.register("soul_sand_glass",
            () -> new SoulSandGlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .mapColor(MapColor.COLOR_BROWN)
                    .instrument(NoteBlockInstrument.FLUTE)
            ));
}
