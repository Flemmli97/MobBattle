package com.flemmli97.mobbattle.items.entityManager;

import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.MobArmy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
	public void render(RenderWorldLastEvent event) {
    		Minecraft mc = Minecraft.getMinecraft();
    		EntityPlayerSP player = mc.player;
    		ItemStack heldItem = player.getHeldItemMainhand();
    		if(heldItem!=null && heldItem.getItem() == ModItems.mobArmy)
    		{
    			MobArmy item = (MobArmy) heldItem.getItem();
    			if(heldItem.getMetadata()==0 || heldItem.getMetadata()==2)
    			{
    				BlockPos pos = item.getSelPos(heldItem)[0];
    				BlockPos pos2 = item.getSelPos(heldItem)[1];
    				if(pos!=null)
    					this.renderBlockOutline(player, pos, pos2, event.getPartialTicks());
    			}
    		}
    }
    
    @SubscribeEvent
    public void addTeamTarget(EntityJoinWorldEvent event)
    {
    		if(event.getEntity() instanceof EntityCreature)
    		{
    			if(event.getEntity().getTeam()!=null && (event.getEntity().getTeam().getRegisteredName().equals("BLUE")|| event.getEntity().getTeam().getRegisteredName().equals("RED")))
    				Team.updateEntity(event.getEntity().getTeam().getRegisteredName(), (EntityCreature) event.getEntity());
    		}
    }
    
    @SubscribeEvent
    public void spawnEggUse(PlayerInteractEvent event)
    {
    		if(!event.getEntityPlayer().world.isRemote && (event instanceof RightClickItem || event instanceof RightClickBlock))
    		{
    			ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
    			ItemStack off = event.getEntityPlayer().getHeldItemOffhand();
    			if(stack.getItem() instanceof ItemMonsterPlacer &&(stack.getDisplayName().equals("BLUE") || stack.getDisplayName().equals("RED")))
    			{
    				event.setCanceled(true);
    				Team.applyTeamModSpawnEgg(event.getEntityPlayer(), stack);
    			}
    			else if(off.getItem() instanceof ItemMonsterPlacer &&(off.getDisplayName().equals("BLUE") || off.getDisplayName().equals("RED")))
    			{
    				event.setCanceled(true);
    				Team.applyTeamModSpawnEgg(event.getEntityPlayer(), off);
    			}
    		}
    }
    
    @SubscribeEvent
    public void teamFriendlyFire(LivingAttackEvent event)
    {
		if(event.getEntity() instanceof EntityCreature && event.getEntity().getTeam()!=null)
		{
			EntityCreature ent = (EntityCreature) event.getEntity();
			if(ent.getTeam()!=null && (ent.getTeam().getRegisteredName().equals("BLUE")|| ent.getTeam().getRegisteredName().equals("RED")))
			{
				if(event.getSource().getEntity() instanceof EntityCreature && event.getSource().getEntity().getTeam()!=null)
				{
					if(ent.isOnSameTeam(event.getSource().getEntity()))
						event.setCanceled(true);
				}
			}
		}
    }
    
    @SubscribeEvent
    public void teamParticle(LivingEvent event)
    {
    		if(event.getEntityLiving() instanceof EntityCreature && event.getEntityLiving().world.isRemote)
    		{
			EntityCreature e =  (EntityCreature) event.getEntityLiving();
    			if(Team.getTeam(e).equals("BLUE"))
    			{
    				e.world.spawnParticle(EnumParticleTypes.REDSTONE, e.posX, e.posY+e.height+0.5, e.posZ, 0.01, 0, 1);
    			}
    			else if(Team.getTeam(e).equals("RED"))
    			{
    				e.world.spawnParticle(EnumParticleTypes.REDSTONE, e.posX, e.posY+e.height+0.5, e.posZ, 0, 0, 0);
    			}
    		}
    }
    
    private void renderBlockOutline(EntityPlayerSP player,  BlockPos pos, BlockPos pos2, float partialTicks)
    {
    		AxisAlignedBB aabb = Team.getBoundingBoxPositions(pos, pos2);
		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks ;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
    		if(aabb!=null)
    		{
    			GlStateManager.pushMatrix();
    			GlStateManager.enableBlend();
    			GlStateManager.disableTexture2D();
            GlStateManager.glLineWidth(2);
            GlStateManager.depthMask(false);
    			RenderGlobal.drawSelectionBoundingBox(aabb.expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2), 1, 0.5F, 0.5F, 1);
    			GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
    			GlStateManager.popMatrix();
    		}	
    }
}
