package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.inv.ContainerArmor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == 0){
            Entity e = player.world.getEntityByID(x);
            if(e instanceof EntityLiving)
                return new ContainerArmor(player.inventory, (EntityLiving) e);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == 0){
            Entity e = player.world.getEntityByID(x);
            if(e instanceof EntityLiving)
                return new GuiArmor(player.inventory, (EntityLiving) e);
        }else if(ID == 1)
            return new GuiEffect();
        return null;
    }

}
