package io.github.flemmli97.mobbattle.common.entity.goal;

import io.github.flemmli97.mobbattle.common.utils.PerimeterData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumSet;

public class PerimeterGoal extends Goal {

    private final Mob mob;

    private PerimeterData.Perimeter perimeter;

    public PerimeterGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        PerimeterData.Perimeter perimeter = PerimeterData.get((ServerLevel) this.mob.level()).perimeter();
        if (perimeter == null || perimeter.contains(this.mob.position())) {
            return false;
        } else {
            this.perimeter = perimeter;
            this.mob.getNavigation().stop();
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        if (this.perimeter == null)
            return;
        if (this.perimeter.shouldTeleport(this.mob.position())) {
            this.teleportTo(this.perimeter.center());
        } else if (this.mob instanceof PathfinderMob path) {
            Vec3 target = Vec3.atBottomCenterOf(this.perimeter.center());
            if (this.mob.distanceToSqr(target) < 11 * 11) {
                this.mob.getNavigation().moveTo(target.x(), target.y(), target.z(), 1);
            } else {
                Vec3 pos = DefaultRandomPos.getPosTowards(path, 10, 7, Vec3.atBottomCenterOf(this.perimeter.center()), Mth.HALF_PI);
                int tries = 0;
                while (pos == null && tries < 10) {
                    pos = DefaultRandomPos.getPosTowards(path, 10, 7, Vec3.atBottomCenterOf(this.perimeter.center()), Mth.HALF_PI);
                    tries++;
                }
                if (pos != null) {
                    this.mob.getNavigation().moveTo(pos.x(), pos.y(), pos.z(), 1);
                }
            }
        }
    }

    private void teleportTo(BlockPos target) {
        BlockPos.MutableBlockPos pos = target.mutable();
        while (pos.getY() < this.mob.level().getMaxY()) {
            Iterable<VoxelShape> collisions = this.mob.level().getBlockCollisions(this.mob, this.mob.getBoundingBox()
                    .move(pos.getX() + 0.5 - this.mob.getX(), pos.getY() - this.mob.getY(), pos.getZ() + 0.5 - this.mob.getZ()));
            if (!collisions.iterator().hasNext()) {
                break;
            }
            for (VoxelShape voxelShape : collisions) {
                if (!voxelShape.isEmpty())
                    break;
            }
            pos.move(Direction.UP);
        }
        this.mob.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.mob.getNavigation().stop();
    }
}