package com.flemmli97.mobbattle.items.entitymanager;

import com.flemmli97.mobbattle.Config;
import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.MobArmy;
import com.flemmli97.mobbattle.items.MobEquip;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    @OnlyIn(value = Dist.CLIENT)
    public void render(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        ItemStack heldItem = player.getHeldItemMainhand();
        if(heldItem.getItem() == ModItems.mobArmy){
            MobArmy item = (MobArmy) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if(pos != null)
                renderBlockOutline(player, pos, pos2, event.getPartialTicks());
        }else if(heldItem.getItem() == ModItems.mobEquip){
            MobEquip item = (MobEquip) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if(pos != null)
                renderBlockOutline(player, pos, pos2, event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void addTeamTarget(EntityJoinWorldEvent event) {
        if(!event.getWorld().isRemote && event.getEntity() instanceof CreatureEntity){
            if(event.getEntity().getTeam() != null)
                Team.updateEntity(event.getEntity().getTeam().getName(), (CreatureEntity) event.getEntity());
            if(event.getEntity().getTags().contains("PickUp"))
                ((CreatureEntity) event.getEntity()).goalSelector.addGoal(10, new EntityAIItemPickup((CreatureEntity) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void teamFriendlyFire(LivingAttackEvent event) {
        if(event.getEntity() instanceof LivingEntity && event.getEntity().getTeam() != null){
            LivingEntity ent = (LivingEntity) event.getEntity();
            if(event.getSource().getTrueSource() instanceof LivingEntity){
                LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
                if(Team.isOnSameTeam(ent, attacker) && !ent.getTeam().getAllowFriendlyFire())
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if(event.getEntity() instanceof CreatureEntity){
            CreatureEntity e = (CreatureEntity) event.getEntity();
            if(e.getTeam() != null){
                if(Config.clientConf.showTeamParticleTypes.get() && e.world.isRemote){
                    RedstoneParticleData color = Team.teamColor.get(e.getTeam().getColor());
                    if(color != null)
                        e.world.addParticle(color, e.posX, e.posY + e.getHeight() + 0.5, e.posZ, 0, 0, 0);
                }else if(Config.serverConf.autoAddAI.get() && !e.getTags().contains("mobbattle:AddedAI")){
                    Team.updateEntity(e.getTeam().getName(), e);
                }
            }
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    private static void renderBlockOutline(ClientPlayerEntity player, BlockPos pos, BlockPos pos2, float partialTicks) {
        AxisAlignedBB aabb = Team.getBoundingBoxPositions(pos, pos2).shrink(0.1);
        ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        double d0 = -activerenderinfo.getProjectedView().x;
        double d1 = -activerenderinfo.getProjectedView().y;
        double d2 = -activerenderinfo.getProjectedView().z;
        if(aabb != null){
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture();
            GlStateManager.lineWidth(2);
            GlStateManager.depthMask(false);
            WorldRenderer.drawSelectionBoundingBox(aabb.grow(0.0020000000949949026D).offset(d0, d1, d2), 1, 0.5F, 0.5F, 1);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
