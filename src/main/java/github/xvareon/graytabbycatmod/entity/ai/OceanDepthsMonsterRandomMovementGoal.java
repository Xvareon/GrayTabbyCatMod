package github.xvareon.graytabbycatmod.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Squid;

public class OceanDepthsMonsterRandomMovementGoal extends Goal {
    private final Squid squid;

    public OceanDepthsMonsterRandomMovementGoal(Squid squid) {
        this.squid = squid;
    }

    public boolean canUse() {
        return true;
    }

    public void tick() {
        if (squid.getTarget() != null) return;
        int noActionTime = squid.getNoActionTime();
        if (noActionTime > 100) {
            squid.setMovementVector(0.0F, 0.0F, 0.0F);
        } else if (squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !squid.isInWater() || !squid.hasMovementVector()) {
            float angle = squid.getRandom().nextFloat() * 6.2831855F;
            float x = Mth.cos(angle) * 0.2F;
            float y = -0.1F + squid.getRandom().nextFloat() * 0.2F;
            float z = Mth.sin(angle) * 0.2F;
            squid.setMovementVector(x, y, z);
        }
    }
}