package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.inv.ContainerArmor;
import com.flemmli97.mobbattle.network.PacketHandler;
import com.flemmli97.mobbattle.network.PacketOpenGuiArmor;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MobArmor extends Item {

    public MobArmor() {
        super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_armor"));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        list.add(new StringTextComponent(TextFormatting.AQUA + "Right click an entity to edit their equipment"));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        return true;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if(target instanceof MobEntity){
            openArmorGUI(player, (MobEntity) target);
            return true;
        }
        return false;
    }

    private static final void openArmorGUI(PlayerEntity player, MobEntity living) {
        if(player.world.isRemote)
            return;
        ServerPlayerEntity mp = (ServerPlayerEntity) player;
        mp.closeContainer();
        mp.getNextWindowId();
        int windowId = mp.currentWindowId;
        PacketHandler.sendToClient(new PacketOpenGuiArmor(living, windowId), mp);
        Container c = new ContainerArmor(windowId, player.inventory, living);
        mp.openContainer = c;
        mp.openContainer.addListener(mp);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(mp, c));
    }
}
