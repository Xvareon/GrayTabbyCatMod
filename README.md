### A simple minecraft mod that adds Gray Tabby Cat variants to your minecraft world along with some misc features.

Added the Gray Tabby Cat mob:
- Spawns in all taiga biomes rarely. This also includes modded biomes which has the #minecraft:is_taiga biome tag.
- Can be tamed with what you can tame vanilla cats with, has a sitting animation, and the collar can be dyed when tamed.
- Can be bred with what you can breed vanilla cats with.
- Comes with different sizes.
- Has 20 variants that have different eye colors PLUS a special variant inspired from minecraft dungeons.
- The cat can attack mobs that the owner attacks by throwing them into the air.
- Can breed with vanilla cats, and the offspring will be a random variant of the parents.

Some extra features this mod includes:
(How are these related to the cat?)

Added  the Doggo mob:
- Spawns in all taiga biomes rarely. This also includes modded biomes which has the #minecraft:is_taiga biome tag.
- Has 14 different variants.
- Can breed with vanilla wolves, and the offspring will be a random variant of the parents.

Added the Barnacle mob. This mob spawns in biomes that have the #is_deep_ocean tag associated with them
- This mob attacks players and fish by pulling them towards it and then attacking them. The player will still get pulled even when on a boat.
- The mob has different sizes and colors to discover!

Added a tamable ocelot mob. Normal ocelots are not replaced, the ocelots spawned by this mod are a separate entity.
- They have combat capabilities, and pretty much has all of a cat's features.
- Spawns in biomes with the #minecraft:is_jungle tag.

Glowing Obsidian:
- Smelt obsidian to obtain this item, using blast furnaces will be 50% faster.
- Hurts entities that step on it like magma blocks all the while burning them.
- Turns into normal obsidian when it comes into contact with water.
- Can be right clicked by an empty bucket, collecting lava from it. The block turns into basalt afterwards.
- Burns infinitely like netherack and magma block.

Soul Sand Glass:
- Obtainable by smelting soul sand! This glass has a unique property making it passable by players but not entities. It acts a lot like a scaffold block but does not obey gravity.
- Slows down entities when they walk inside, above it, or stick below it (You shift to unstick).
- Lighting it up with fire makes it ignite with soul fire.
- Can be broken with bare hands or any tool, will always drop.
- It gives a weaker bubble column but can be stacked on top of each other while in water.

Lightning Charge:
- Crafted with 8 fire charges and a prismarine crystal at the middle.
- Summons a lightning bolt upon hit. This deals an additional damage to the first mob it hits.
- Stackable, Dispensable, Throwable via right click

Dragon Fireball:
- This is an item version of the ender dragon's fireball attack.
- Crafted with 8 fire charges and a dragon's breath at the middle.
- Summons an aoe cloud upon hit similar to the dragon fire ball, but this afflicts entities in the cloud instant wither 4 so it can now damage undead. This also deals an additional damage to the first mob it hits.
- Stackable, Dispensable, Throwable via right click

## [For more info, visit the official mod page here.](https://legacy.curseforge.com/minecraft/mc-mods/graytabbycatmod)

-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code (aka
srgnames) - this means that you will not be able to read them directly against
normal code.

Source pack installation information:

Standalone source installation
==============================

See the Forge Documentation online for more detailed instructions:
http://mcforge.readthedocs.io/en/latest/gettingstarted/

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: "gradlew genEclipseRuns" (./gradlew genEclipseRuns if you are on Mac/Linux)
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run "gradlew eclipse" to generate the project.
(Current Issue)
4. Open Project > Run/Debug Settings > Edit runClient and runServer > Environment
5. Edit MOD_CLASSES to show [modid]%%[Path]; 2 times rather then the generated 4.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: "gradlew genIntellijRuns" (./gradlew genIntellijRuns if you are on Mac/Linux)
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can run "gradlew --refresh-dependencies" to refresh the local cache. "gradlew clean" to reset everything {this does not affect your code} and then start the processs again.

Should it still not work, 
Refer to #ForgeGradle on EsperNet for more information about the gradle environment.
or the Forge Project Discord discord.gg/UvedJ9m

Forge source installation
=========================
MinecraftForge ships with this code and installs it as part of the forge
installation process, no further action is required on your part.

LexManos' Install Video
=======================
https://www.youtube.com/watch?v=8VEdtQLuLO0&feature=youtu.be

Turtywurty's Setup Video
=======================
https://www.youtube.com/watch?v=334wakLzQFY

For more details update more often refer to the Forge Forums:
http://www.minecraftforge.net/forum/index.php/topic,14048.0.html

To export your jar file:
- Modify your gradle.properties to suit the jar file's name and version.
- Go to gradle: Tasks->build->build.
- The jar file should be located in your build->libs
