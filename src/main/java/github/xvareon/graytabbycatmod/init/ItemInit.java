package github.xvareon.graytabbycatmod.init;

import github.xvareon.graytabbycatmod.GrayTabbyCatMod;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static github.xvareon.graytabbycatmod.init.CreativeTabInit.addToTab;
public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GrayTabbyCatMod.MODID);
    public static final RegistryObject<ForgeSpawnEggItem> GRAY_TABBY_CAT_SPAWN_EGG = addToTab(ITEMS.register("gray_tabby_cat_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityInit.GRAY_TABBY_CAT, 0x424242, 0x1A1919, new Item.Properties())));
}
