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
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumSet;

public class PerimeterGoal extends Goal {

    private final Mob mob;

    private PerimeterData.Perimeter perimeter;

    private Path path;

    public PerimeterGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        PerimeterData.Perimeter perimeter = PerimeterData.get((ServerLevel) this.mob.level()).perimeter();
        if (perimeter == null || perimeter.contains(this.mob.position())) {
            return false;
        } else {
            this.perimeter = perimeter;
            if (this.perimeter.shouldTeleport(this.mob.position())) {
                this.path = null;
                return true;
            }
            Vec3 target = Vec3.atBottomCenterOf(this.perimeter.center());
            Path path = null;
            if (this.mob.distanceToSqr(target) < 11 * 11) {
                path = this.mob.getNavigation().createPath(target.x(), target.y(), target.z(), 1);
            } else {
                Vec3 pos;
                if (this.mob instanceof PathfinderMob pathfinderMob) {
                    pos = DefaultRandomPos.getPosTowards(pathfinderMob, 10, 7, Vec3.atBottomCenterOf(this.perimeter.center()), Mth.HALF_PI);
                    int tries = 0;
                    while (pos == null && tries < 10) {
                        pos = DefaultRandomPos.getPosTowards(pathfinderMob, 10, 7, Vec3.atBottomCenterOf(this.perimeter.center()), Mth.HALF_PI);
                        tries++;
                    }
                } else {
                    pos = Vec3.atCenterOf(this.perimeter.center());
                }
                if (pos != null) {
                    path = this.mob.getNavigation().createPath(pos.x(), pos.y(), pos.z(), 1);
                }
            }
            this.path = path;
            return this.path != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.path = null;
    }

    @Override
    public void start() {
        if (this.perimeter == null)
            return;
        this.mob.getNavigation().stop();
        if (this.path == null) {
            this.teleportTo(this.perimeter.center());
        } else {
            this.mob.getNavigation().moveTo(this.path, 1);
        }
    }

    private void teleportTo(BlockPos target) {
        BlockPos.MutableBlockPos pos = target.mutable();
        while (pos.getY() < this.mob.level().getMaxBuildHeight()) {
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