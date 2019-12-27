package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.inv.ContainerArmor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
        if(ID == 0){
            Entity e = player.world.getEntityByID(x);
            if(e instanceof MobEntity)
                return new ContainerArmor(y, player.inventory, (MobEntity) e);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
        if(ID == 0){
            Entity e = player.world.getEntityByID(x);
            if(e instanceof MobEntity)
                return new GuiArmor(y, player.inventory, (MobEntity) e);
        }else if(ID == 1)
            return new GuiEffect();
        return null;
    }

}
