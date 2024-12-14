package github.xvareon.graytabbycatmod.entity;

import github.xvareon.graytabbycatmod.init.EntityInit;
import github.xvareon.graytabbycatmod.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class LightningCharge extends ThrowableItemProjectile {

    public LightningCharge(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LightningCharge(Level pLevel) {
        super(EntityInit.LIGHTNING_CHARGE.get(), pLevel);
    }

    public LightningCharge(Level pLevel, LivingEntity livingEntity) {
        super(EntityInit.LIGHTNING_CHARGE.get(), livingEntity, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemInit.LIGHTNING_CHARGE.get();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (pResult.getType() != HitResult.Type.ENTITY || !this.ownedBy(((EntityHitResult)pResult).getEntity())) {
            if (!this.level().isClientSide) {
                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(this.level());
                if (lightningBolt != null) {
                    lightningBolt.moveTo(this.getX(), this.getY(), this.getZ());
                    this.level().addFreshEntity(lightningBolt);
                }
                this.discard();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!this.level().isClientSide) {
            Entity hitEntity = entityHitResult.getEntity();

            hitEntity.hurt(damageSources().thrown(this, this.getOwner()), 10.0F);

            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    public ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    public boolean shouldBurn() {
        return false;
    }
}
