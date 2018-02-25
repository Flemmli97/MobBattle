package com.flemmli97.mobbattle.items.entityManager;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Team {
    
    public static boolean isOppositeTeam(Entity entity, Entity target)
    {
    		if(entity.getTeam()==null ||target.getTeam()==null)
    			return false;
    		if(entity.getTeam().getName().equals("RED") && target.getTeam().getName().equals("BLUE"))
    			return true;
    		if(entity.getTeam().getName().equals("BLUE") && target.getTeam().getName().equals("RED"))
    			return true;
    		return false;
    }
    
    public static String getTeam(Entity entity)
    {
    		return entity.getTeam()!=null?entity.getTeam().getName():"none";
    }
    
    public static void addEntityToTeam(Entity entity, String team)
    {
    		Scoreboard score = entity.world.getScoreboard();
    		if(score.getTeam(team)==null)
    			score.createTeam(team);
    		score.addPlayerToTeam(entity.getCachedUniqueIdString(), team);
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
}
