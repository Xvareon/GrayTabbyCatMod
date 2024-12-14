package github.xvareon.graytabbycatmod.entity;

import github.xvareon.graytabbycatmod.init.EntityInit;
import github.xvareon.graytabbycatmod.init.ItemInit;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class DragonFireball extends ThrowableItemProjectile {

    public DragonFireball(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DragonFireball(Level pLevel) {
        super(EntityInit.DRAGON_FIREBALL.get(), pLevel);
    }

    public DragonFireball(Level pLevel, LivingEntity livingEntity) {
        super(EntityInit.DRAGON_FIREBALL.get(), livingEntity, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemInit.DRAGON_FIREBALL.get();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (pResult.getType() != HitResult.Type.ENTITY || !this.ownedBy(((EntityHitResult)pResult).getEntity())) {
            if (!this.level().isClientSide) {
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
                AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
                Entity entity = this.getOwner();

                areaeffectcloud.setOwner((LivingEntity) entity);
                areaeffectcloud.setParticle(ParticleTypes.DRAGON_BREATH);
                areaeffectcloud.setRadius(3.0F);
                areaeffectcloud.setDuration(600);
                areaeffectcloud.setRadiusPerTick((7.0F - areaeffectcloud.getRadius()) / (float)areaeffectcloud.getDuration());
                areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.WITHER, 5, 3));
                if (!list.isEmpty()) {
                    for(LivingEntity targetentity : list) {
                        double d0 = this.distanceToSqr(targetentity);
                        if (d0 < 16.0D) {
                            areaeffectcloud.setPos(targetentity.getX(), targetentity.getY(), targetentity.getZ());
                            targetentity.hurt(damageSources().thrown(this, this.getOwner()), 10.0F);
                            break;
                        }
                    }
                }

                this.level().levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
                this.level().addFreshEntity(areaeffectcloud);
                this.discard();
            }

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
