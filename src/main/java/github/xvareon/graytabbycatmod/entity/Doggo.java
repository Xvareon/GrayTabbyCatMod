package github.xvareon.graytabbycatmod.entity;

import github.xvareon.graytabbycatmod.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Doggo extends Wolf {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Doggo.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> COLLAR_COLOR = SynchedEntityData.defineId(Doggo.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> SIZE_MULTIPLIER = SynchedEntityData.defineId(Doggo.class, EntityDataSerializers.FLOAT);

    public Doggo(EntityType<Doggo> type, Level level) {
        super(type, level);

        if (!level.isClientSide) {
            DoggoVariant variant = DoggoVariant.getRandomVariant(random);
            setDoggoVariant(variant);
            setSizeMultiplier(variant.getDefaultSize());
        }

        this.refreshDimensions(); // Update hitbox

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, Wolf.class));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SIZE_MULTIPLIER, 1.0f);
        entityData.define(VARIANT, DoggoVariant.OGGIE.ordinal());
        entityData.define(COLLAR_COLOR, DyeColor.GRAY.getId());
    }

    @NotNull
    public DoggoVariant getDoggoVariant() {
        return DoggoVariant.byId(entityData.get(VARIANT));
    }

    public void setDoggoVariant(DoggoVariant variant) {
        entityData.set(VARIANT, variant.getId());
    }

    public @NotNull DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor collarcolor) {
        this.entityData.set(COLLAR_COLOR, collarcolor.getId());
    }

    public float getSizeMultiplier() {
        return entityData.get(SIZE_MULTIPLIER);
    }

    public void setSizeMultiplier(float size) {
        entityData.set(SIZE_MULTIPLIER, size);
        this.refreshDimensions();
    }

    @NotNull
    @Override
    public EntityDimensions getDimensions(@NotNull Pose pose) {
        return super.getDimensions(pose).scale(getSizeMultiplier());
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.refreshDimensions(); // Ensure hitbox updates
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("SizeMultiplier", this.getSizeMultiplier());
        compound.putInt("DoggoVariant", this.getDoggoVariant().getId()); // Save variant ID
        compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SizeMultiplier")) {
            this.setSizeMultiplier(compound.getFloat("SizeMultiplier"));
        }
        if (compound.contains("DoggoVariant")) {
            this.setDoggoVariant(DoggoVariant.byId(compound.getInt("DoggoVariant"))); // Load variant ID
        }
        if (compound.contains("CollarColor")){
            this.setCollarColor(DyeColor.byId(compound.getByte("CollarColor")));
        }
    }

    // For Dye
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (this.isTame()){
            if (item instanceof DyeItem) {
                DyeColor dyecolor = ((DyeItem) item).getDyeColor();
                if (dyecolor != this.getCollarColor()) {
                    this.setCollarColor(dyecolor);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.mobInteract(player, hand);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Wolf.createAttributes();
    }

    public static boolean canSpawn(EntityType<Doggo> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return Wolf.checkAnimalSpawnRules(entityType, level, spawnType, position, random);
    }

    @Override
    public boolean canMate(@NotNull Animal other) {
        if (other == this) return false;
        if (!(other instanceof Wolf otherWolf)) return false;
        return this.isTame() && otherWolf.isTame() && this.isInLove() && otherWolf.isInLove();
    }

    @Override
    public Wolf getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        Wolf baby;

        // 1. Doggo × Doggo → always Doggo
        if (otherParent instanceof Doggo) {
            baby = new Doggo(EntityInit.DOGGO.get(), level);
        }
        // 2. Doggo × Vanilla Wolf → 50/50 chance
        else if (otherParent instanceof Wolf vanillaParent) {
            boolean isDoggo = level.getRandom().nextBoolean();
            if (isDoggo) {
                baby = new Doggo(EntityInit.DOGGO.get(), level);
            } else {
                baby = EntityType.WOLF.create(level);
            }
        } else {
            return null;
        }

        // 3. Apply shared properties once
        if (baby != null) {
            baby.setPos(this.getX(), this.getY(), this.getZ());

            UUID ownerUuid = this.getOwnerUUID();
            if (ownerUuid != null) {
                baby.setOwnerUUID(ownerUuid);
                baby.setTame(true);
            }
        }

        return baby;
    }
}