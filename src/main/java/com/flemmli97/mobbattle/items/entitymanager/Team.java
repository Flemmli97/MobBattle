package com.flemmli97.mobbattle.items.entitymanager;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.CollisionRule;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Team {
    
	public static Map<TextFormatting, double[]> teamColor = Maps.newHashMap();
	static
	{
		teamColor.put(TextFormatting.AQUA, new double[] {0.01,0.9,1});
		teamColor.put(TextFormatting.BLACK, new double[] {0.01,0,0});
		teamColor.put(TextFormatting.BLUE, new double[] {0.2,0.2,1});
		teamColor.put(TextFormatting.DARK_AQUA, new double[] {0.01,0.4,0.5});
		teamColor.put(TextFormatting.DARK_BLUE, new double[] {0.01,0,0.4});
		teamColor.put(TextFormatting.DARK_GRAY, new double[] {0.2,0.2,0.2});
		teamColor.put(TextFormatting.DARK_GREEN,  new double[] {0.01,0.5,0});
		teamColor.put(TextFormatting.DARK_PURPLE, new double[] {0.3,0,0.4});
		teamColor.put(TextFormatting.DARK_RED, new double[] {0.5,0,0});
		teamColor.put(TextFormatting.GOLD, new double[] {1,0.6,0});
		teamColor.put(TextFormatting.GRAY, new double[] {0.4,0.4,0.4});
		teamColor.put(TextFormatting.GREEN, new double[] {0.01,1,0});
		teamColor.put(TextFormatting.LIGHT_PURPLE, new double[] {0.6,0,0.7});
		teamColor.put(TextFormatting.RED, new double[] {1,0.2,0.2});
		teamColor.put(TextFormatting.WHITE, new double[] {1,1,1});
		teamColor.put(TextFormatting.YELLOW, new double[] {1,1,0});
	}
    
    public static String getTeam(Entity entity)
    {
    		return entity.getTeam()!=null?entity.getTeam().getRegisteredName():"none";
    }
    
    public static boolean isOnSameTeam(Entity entity, Entity entity2)
    {
    	if(entity.getTeam()==null)
    		return true;
    	if(entity2.getTeam()==null)
    		return true;
    	return entity.isOnSameTeam(entity2);
    }
    
    public static void addEntityToTeam(Entity entity, String team)
    {
    		Scoreboard score = entity.worldObj.getScoreboard();
    		if(score.getTeam(team)==null)
    		{
    			score.createTeam(team);
    			score.getTeam(team).setCollisionRule(CollisionRule.HIDE_FOR_OTHER_TEAMS);
    		}
    		score.addPlayerToTeam(entity.getCachedUniqueIdString(), team);
    }
    
    public static void updateEntity(String team, EntityCreature e)
	{
		Team.addEntityToTeam(e, team);
		e.targetTasks.taskEntries.removeIf(new Predicate<EntityAITaskEntry>() {
			@Override
			public boolean test(EntityAITaskEntry input) {
				return input.action instanceof EntityAITarget;
			}});
		e.setAttackTarget(null);
		e.setHealth(e.getMaxHealth());
		e.targetTasks.addTask(1, new EntityAITeamTarget(e, false, true));
	}
    
    public static AxisAlignedBB getBoundingBoxPositions(BlockPos pos, @Nullable BlockPos pos2)
    {
    		if(pos2 ==null)
    		{
    		    return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(pos);
    		}
    		else
    		{
    			int xDiff = pos.getX()-pos2.getX();
    			int yDiff = pos.getY()-pos2.getY();
    			int zDiff = pos.getZ()-pos2.getZ();
    			int x, y, z, x2, y2, z2;
    			x=y=z=x2=y2=z2=0;
    			if(xDiff<=0) x = 1;	
    			else x2=1;
    			if(yDiff<=0) y = 1;
    			else y2=1;
    			if(zDiff<=0) z=1;
    			else z2=1;
    			AxisAlignedBB bb = new AxisAlignedBB(0+x, 0+y, 0+z, xDiff+x2, yDiff+y2, zDiff+z2).offset(pos2);
    			return bb;
    		}
    }
    
    public static EntityLiving fromUUID(World world, String uuid)
    {
    		if(uuid!=null)
    		for(Entity entity : world.loadedEntityList)
    			if(entity.getCachedUniqueIdString()==uuid && entity instanceof EntityLiving)
    				return (EntityLiving) entity;
    		return null;
    }
    
    public static double getYOffset(World world, BlockPos pos)
    {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(pos)).expand(0.0D, -1.0D, 0.0D);
        List<AxisAlignedBB> list = world.getCollisionBoxes((Entity)null, axisalignedbb);

        if (list.isEmpty())
        {
            return 0.0D;
        }
        else
        {
            double d0 = axisalignedbb.minY;

            for (AxisAlignedBB axisalignedbb1 : list)
            {
                d0 = Math.max(axisalignedbb1.maxY, d0);
            }

            return d0 - (double)pos.getY();
        }
    }
    
    public static RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0;
        if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP)
        {
            d3 = ((net.minecraft.entity.player.EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }
    
    public static void applyTeamModSpawnEgg(EntityPlayer player, ItemStack stack)
    {
    		RayTraceResult raytraceresult = Team.rayTrace(player.worldObj, player, true);

        if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos blockpos = raytraceresult.getBlockPos();
            boolean liquid = player.worldObj.getBlockState(blockpos).getBlock() instanceof BlockLiquid;
            if(!(liquid))
            {
            		blockpos = blockpos.offset(raytraceresult.sideHit);
            		blockpos = new BlockPos (blockpos.getX(), blockpos.getY()+ Team.getYOffset(player.worldObj, blockpos), blockpos.getZ());
            }
            if (player.worldObj.isBlockModifiable(player, blockpos) && player.canPlayerEdit(blockpos, raytraceresult.sideHit, stack))
            {
                Entity entity = ItemMonsterPlacer.spawnCreature(player.worldObj, ItemMonsterPlacer.getEntityIdFromItem(stack), (double)blockpos.getX() + (liquid?0.5D:0), (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
                if (entity != null)
                {
                    ItemMonsterPlacer.applyItemEntityDataToEntity(player.worldObj, player, stack, entity);
                    if (!player.capabilities.isCreativeMode)
                    {
                    		--stack.stackSize;
                    }
                    player.addStat(StatList.getObjectUseStats(stack.getItem()));
                    if (entity instanceof EntityCreature && stack.getDisplayName().equals("BLUE") || stack.getDisplayName().equals("RED"))
                    {
                        Team.updateEntity(stack.getDisplayName(), (EntityCreature) entity);
                    }
                }
                
            }
        }
    }
}
