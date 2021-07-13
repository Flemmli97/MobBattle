package com.flemmli97.mobbattle.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.CollisionRule;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class Utils {

    public static Map<TextFormatting, RedstoneParticleData> teamColor = new HashMap<>();

    static {
        teamColor.put(TextFormatting.AQUA, new RedstoneParticleData(0.01f, 0.9f, 1f, 1.0f));
        teamColor.put(TextFormatting.BLACK, new RedstoneParticleData(0.01f, 0, 0f, 1.0f));
        teamColor.put(TextFormatting.BLUE, new RedstoneParticleData(0.2f, 0.2f, 1, 1.0f));
        teamColor.put(TextFormatting.DARK_AQUA, new RedstoneParticleData(0.01f, 0.4f, 0.5f, 1.0f));
        teamColor.put(TextFormatting.DARK_BLUE, new RedstoneParticleData(0.01f, 0, 0.4f, 1.0f));
        teamColor.put(TextFormatting.DARK_GRAY, new RedstoneParticleData(0.2f, 0.2f, 0.2f, 1.0f));
        teamColor.put(TextFormatting.DARK_GREEN, new RedstoneParticleData(0.01f, 0.5f, 0, 1.0f));
        teamColor.put(TextFormatting.DARK_PURPLE, new RedstoneParticleData(0.3f, 0, 0.4f, 1.0f));
        teamColor.put(TextFormatting.DARK_RED, new RedstoneParticleData(0.5f, 0, 0, 1.0f));
        teamColor.put(TextFormatting.GOLD, new RedstoneParticleData(1, 0.6f, 0, 1.0f));
        teamColor.put(TextFormatting.GRAY, new RedstoneParticleData(0.4f, 0.4f, 0.4f, 1.0f));
        teamColor.put(TextFormatting.GREEN, new RedstoneParticleData(0.01f, 1, 0, 1.0f));
        teamColor.put(TextFormatting.LIGHT_PURPLE, new RedstoneParticleData(0.6f, 0, 0.7f, 1.0f));
        teamColor.put(TextFormatting.RED, new RedstoneParticleData(1, 0.2f, 0.2f, 1.0f));
        teamColor.put(TextFormatting.WHITE, new RedstoneParticleData(1, 1, 1, 1.0f));
        teamColor.put(TextFormatting.YELLOW, new RedstoneParticleData(1, 1, 0, 1.0f));
    }

    public static String getTeam(Entity entity) {
        return entity.getTeam() != null ? entity.getTeam().getName() : "none";
    }

    public static boolean isOnSameTeam(Entity entity, Entity entity2) {
        if (entity.getTeam() == null)
            return true;
        if (entity2.getTeam() == null)
            return true;
        return entity.isOnSameTeam(entity2);
    }

    public static void addEntityToTeam(Entity entity, String team) {
        Scoreboard score = entity.world.getScoreboard();
        ScorePlayerTeam scoreTeam = score.getTeam(team);
        if (scoreTeam == null) {
            scoreTeam = score.createTeam(team);
            scoreTeam.setCollisionRule(CollisionRule.PUSH_OTHER_TEAMS);
        }
        score.getTeam(team).getMembershipCollection().size();
        score.addPlayerToTeam(entity.getCachedUniqueIdString(), scoreTeam);
    }

    public static int getTeamSize(Entity entity, String team) {
        return entity.world.getScoreboard().getTeam(team) != null ? entity.world.getScoreboard().getTeam(team).getMembershipCollection().size() : 0;
    }

    private static final Predicate<Goal> targetGoal = (goal) -> true;

    public static void updateEntity(String team, MobEntity e) {
        addEntityToTeam(e, team);
        removeGoal(e.targetSelector, targetGoal);
        e.setAttackTarget(null);
        e.targetSelector.addGoal(0, new EntityAITeamTarget(e, false, true));
        e.addTag("mobbattle:AddedAI");
    }

    private static final Field goalSelector_goal = ObfuscationReflectionHelper.findField(GoalSelector.class, "goals");

    /**
     * like {@link GoalSelector#removeGoal(Goal)} but with a predicate
     */
    @SuppressWarnings("unchecked")
    private static void removeGoal(GoalSelector goalSel, Predicate<Goal> pred) {
        try {
            Set<PrioritizedGoal> goals = (Set<PrioritizedGoal>) goalSelector_goal.get(goalSel);
            goals.stream().filter((prioGoal) -> pred.test(prioGoal.getGoal())).filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::resetTask);
            goals.removeIf((prioGoal) -> pred.test(prioGoal.getGoal()));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static AxisAlignedBB getBoundingBoxPositions(BlockPos pos, @Nullable BlockPos pos2) {
        if (pos2 == null) {
            return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(pos);
        } else {
            int xDiff = pos.getX() - pos2.getX();
            int yDiff = pos.getY() - pos2.getY();
            int zDiff = pos.getZ() - pos2.getZ();
            int x, y, z, x2, y2, z2;
            x = y = z = x2 = y2 = z2 = 0;
            if (xDiff <= 0)
                x = 1;
            else
                x2 = 1;
            if (yDiff <= 0)
                y = 1;
            else
                y2 = 1;
            if (zDiff <= 0)
                z = 1;
            else
                z2 = 1;
            return new AxisAlignedBB(x, y, z, xDiff + x2, yDiff + y2, zDiff + z2).offset(pos2);
        }
    }

    public static MobEntity fromUUID(ServerWorld world, String uuid) {
        if (uuid != null) {
            Entity e = world.getEntityByUuid(UUID.fromString(uuid));
            if (e instanceof MobEntity)
                return (MobEntity) e;
        }
        return null;
    }

    public static RayTraceResult rayTrace(World worldIn, PlayerEntity playerIn, FluidMode fluidmode) {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.getPosX();
        double d1 = playerIn.getPosY() + (double) playerIn.getEyeHeight();
        double d2 = playerIn.getPosZ();
        Vector3d vec3d = new Vector3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = playerIn.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        Vector3d vec3d1 = vec3d.add((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
        RayTraceContext ctx = new RayTraceContext(vec3d1, vec3d1, BlockMode.OUTLINE, fluidmode, playerIn);
        return worldIn.rayTraceBlocks(ctx);
    }

    public static void setAttackTarget(MobEntity entity, LivingEntity target, boolean both) {
        if (target == null)
            return;
        entity.setAttackTarget(target);
        entity.getBrain().replaceMemory(MemoryModuleType.ANGRY_AT, target.getUniqueID(), 600);
        entity.getBrain().replaceMemory(MemoryModuleType.ATTACK_TARGET, target, 600);
        if (target instanceof MobEntity && both) {
            ((MobEntity) target).setAttackTarget(entity);
            target.getBrain().replaceMemory(MemoryModuleType.ANGRY_AT, entity.getUniqueID(), 600);
            target.getBrain().replaceMemory(MemoryModuleType.ATTACK_TARGET, entity, 600);
        }
    }
}
