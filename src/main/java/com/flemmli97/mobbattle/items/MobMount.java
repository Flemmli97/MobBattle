package com.flemmli97.mobbattle.items;

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
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobMount extends ItemSword {

    public MobMount() {
        super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_mount");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_mount"));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack p_77661_1_) {
        return EnumAction.NONE;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return HashMultimap.create();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag b) {
        list.add(TextFormatting.AQUA + "Left click an entity to select");
        list.add(TextFormatting.AQUA + "Left click another entity to add selected entity as rider");
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("StoredEntity");
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if(entity instanceof EntityLiving && !player.world.isRemote){
            if(stack.hasTagCompound() && stack.getTagCompound().hasKey("StoredEntity")){
                EntityLiving storedEntity = Team.fromUUID(player.world, stack.getTagCompound().getString("StoredEntity"));
                if(storedEntity != null && storedEntity != entity && !this.passengerContainsEntity(storedEntity, entity)){
                    storedEntity.startRiding(entity);
                    stack.getTagCompound().removeTag("StoredEntity");
                }
            }else{
                NBTTagCompound compound = new NBTTagCompound();
                if(stack.hasTagCompound())
                    compound = stack.getTagCompound();
                compound.setString("StoredEntity", entity.getCachedUniqueIdString());
                stack.setTagCompound(compound);
            }
        }
        return true;
    }

    private boolean passengerContainsEntity(Entity theEntity, Entity entitySearch) {
        if(!theEntity.getPassengers().isEmpty())
            if(theEntity.getPassengers().contains(entitySearch))
                return true;
            else
                return this.passengerContainsEntity(theEntity.getPassengers().get(0), entitySearch);
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
