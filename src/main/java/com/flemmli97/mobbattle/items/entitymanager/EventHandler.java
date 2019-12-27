package com.flemmli97.mobbattle.items.entitymanager;

import com.flemmli97.mobbattle.Config;
import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.MobArmy;
import com.flemmli97.mobbattle.items.MobEquip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {

    @SubscribeEvent
    @SideOnly(value = Side.CLIENT)
    public void render(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        ItemStack heldItem = player.getHeldItemMainhand();
        if(heldItem != null && heldItem.getItem() == ModItems.mobArmy){
            MobArmy item = (MobArmy) heldItem.getItem();
            if(heldItem.getMetadata() == 0 || heldItem.getMetadata() == 2){
                BlockPos pos = item.getSelPos(heldItem)[0];
                BlockPos pos2 = item.getSelPos(heldItem)[1];
                if(pos != null)
                    this.renderBlockOutline(player, pos, pos2, event.getPartialTicks());
            }
        }else if(heldItem != null && heldItem.getItem() == ModItems.mobEquip){
            MobEquip item = (MobEquip) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if(pos != null)
                this.renderBlockOutline(player, pos, pos2, event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void addTeamTarget(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof EntityCreature){
            if(event.getEntity().getTeam() != null)
                Team.updateEntity(event.getEntity().getTeam().getName(), (EntityCreature) event.getEntity());
            if(event.getEntity().getTags().contains("PickUp"))
                ((EntityCreature) event.getEntity()).tasks.addTask(10, new EntityAIItemPickup((EntityCreature) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void teamFriendlyFire(LivingAttackEvent event) {
        if(event.getEntity() instanceof EntityLivingBase && event.getEntity().getTeam() != null){
            EntityLivingBase ent = (EntityLivingBase) event.getEntity();
            if(event.getSource().getTrueSource() instanceof EntityLivingBase){
                EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
                if(Team.isOnSameTeam(ent, attacker) && !ent.getTeam().getAllowFriendlyFire())
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if(event.getEntityLiving() instanceof EntityCreature){
            EntityCreature e = (EntityCreature) event.getEntityLiving();
            if(e.getTeam() != null){
                if(Config.showTeamParticles && e.world.isRemote){
                    double[] color = Team.teamColor.get(e.getTeam().getColor());
                    if(color != null)
                        e.world.spawnParticle(EnumParticleTypes.REDSTONE, e.posX, e.posY + e.height + 0.5, e.posZ, color[0], color[1], color[2]);
                }else if(Config.autoAddAI && !e.getTags().contains("mobbattle:AddedAI")){
                    Team.updateEntity(e.getTeam().getName(), e);
                }
            }
        }
    }

    @SideOnly(value = Side.CLIENT)
    private void renderBlockOutline(EntityPlayerSP player, BlockPos pos, BlockPos pos2, float partialTicks) {
        AxisAlignedBB aabb = Team.getBoundingBoxPositions(pos, pos2).shrink(0.1);
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        if(aabb != null){
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.glLineWidth(2);
            GlStateManager.depthMask(false);
            RenderGlobal.drawSelectionBoundingBox(aabb.grow(0.0020000000949949026D).offset(-d0, -d1, -d2), 1, 0.5F, 0.5F, 1);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
