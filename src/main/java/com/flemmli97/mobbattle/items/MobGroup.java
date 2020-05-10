package com.flemmli97.mobbattle.items;

import java.util.ArrayList;
import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.entitymanager.Team;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobGroup extends ItemSword {

    public MobGroup() {
        super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_group");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_group"));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return HashMultimap.create();
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag b) {
        list.add(TextFormatting.AQUA + "Left click to select entities");
        list.add(TextFormatting.AQUA + "Right click on entity to set the target");
        list.add(TextFormatting.AQUA + "Shift right click to reset");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if(!player.isSneaking() && !player.world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("EntityList")){
            NBTTagList list = stack.getTagCompound().getTagList("EntityList", 8);
            for(int i = 0; i < list.tagCount(); i++){
                EntityLiving e = Team.fromUUID(player.world, list.getStringTagAt(i));
                if(entity != e && e != null){
                    EntityLiving living = (EntityLiving) entity;
                    living.setAttackTarget(e);
                    e.setAttackTarget(living);
                }
            }
            stack.getTagCompound().removeTag("EntityList");
            player.setHeldItem(hand, stack);
        }
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(!player.world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("EntityList")){
            if(!player.isSneaking() && stack.getTagCompound().getTagList("EntityList", 8).tagCount() > 0){
                NBTTagList list = stack.getTagCompound().getTagList("EntityList", 8);
                list.removeTag(list.tagCount() - 1);
                stack.getTagCompound().setTag("EntityList", list);
                player.sendMessage(new TextComponentString(TextFormatting.RED + "Removed an entity"));
            }else{
                stack.getTagCompound().removeTag("EntityList");
                player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset all entities"));
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if(entity instanceof EntityLiving && !player.world.isRemote){
            NBTTagCompound compound = new NBTTagCompound();
            if(stack.hasTagCompound())
                compound = stack.getTagCompound();
            ArrayList<String> list = new ArrayList<String>();

            if(compound.hasKey("EntityList")){
                for(int i = 0; i < compound.getTagList("EntityList", 8).tagCount(); i++){
                    list.add(compound.getTagList("EntityList", 8).getStringTagAt(i));
                }
            }
            if(!list.contains(entity.getCachedUniqueIdString())){
                NBTTagList nbttaglist = new NBTTagList();
                if(compound.hasKey("EntityList"))
                    nbttaglist = compound.getTagList("EntityList", 8);
                nbttaglist.appendTag(new NBTTagString(entity.getCachedUniqueIdString()));
                compound.setTag("EntityList", nbttaglist);
                stack.setTagCompound(compound);
                player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Added an entity"));
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
