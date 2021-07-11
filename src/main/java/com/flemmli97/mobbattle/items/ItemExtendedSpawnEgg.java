package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.items.entitymanager.Utils;
import com.google.common.base.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class ItemExtendedSpawnEgg extends Item {

    public static final String tagString = MobBattle.MODID + ":Entity";

    public ItemExtendedSpawnEgg() {
        super(new Item.Properties().group(MobBattleTab.customTab));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        list.add(new TranslationTextComponent("tooltip.spawnegg").mergeStyle(TextFormatting.AQUA));
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            CompoundNBT compound = stack.getTag().getCompound(tagString);
            String entity = EntityType.byKey(compound.getString("id")).isPresent()
                    ? EntityType.byKey(compound.getString("id")).get().getTranslationKey()
                    : "";
            if (!entity.isEmpty()) {
                String entityName = compound.contains("CustomName") ? compound.getString("CustomName") : I18n.format(entity);
                list.add(new TranslationTextComponent("tooltip.spawnegg.spawn", entityName + (compound.size() > 1 ? " (+NBT)" : "")).mergeStyle(TextFormatting.GOLD));
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity) {
            if (player.abilities.isCreativeMode)//|| player.canUseCommand(2, ""))
            {
                MobEntity e = (MobEntity) entity;
                boolean nbt = false;
                CompoundNBT compound = stack.getTag();
                if (compound == null)
                    compound = new CompoundNBT();
                CompoundNBT tag = new CompoundNBT();
                if (player.isSneaking()) {
                    e.writeUnlessPassenger(tag);
                    this.removeMobSpecifigTags(tag);
                    nbt = true;
                } else {
                    String name = e.getEntityString();
                    if (name != null)
                        tag.putString("id", name);
                    /*
                     * if(CommonProxy.mca && e instanceof EntityVillagerMCA) { tag.putInteger("MCAGender",
                     * ((EntityVillagerMCA)e).attributes.getGender().getId()); }
                     */
                }
                compound.put(tagString, tag);
                stack.setTag(compound);

                if (!player.world.isRemote) {
                    player.sendMessage(new TranslationTextComponent("tooltip.spawnegg.save", (nbt ? " + nbt" : "")).mergeStyle(TextFormatting.GOLD), player.getUniqueID());
                }
            } else {
                player.sendMessage(new TranslationTextComponent("tooltip.spawnegg.creative").mergeStyle(TextFormatting.DARK_RED), player.getUniqueID());
            }
        }
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        if (ctx.getWorld().isRemote)
            return ActionResultType.SUCCESS;
        ItemStack itemstack = ctx.getItem();
        if (!ctx.getPlayer().canPlayerEdit(ctx.getPos().offset(ctx.getFace()), ctx.getFace(), itemstack))
            return ActionResultType.FAIL;
        BlockState iblockstate = ctx.getWorld().getBlockState(ctx.getPos());
        if (hasSavedEntity(itemstack)) {
            TileEntity tile = ctx.getWorld().getTileEntity(ctx.getPos());
            if (tile instanceof MobSpawnerTileEntity) {
                MobSpawnerTileEntity spawner = (MobSpawnerTileEntity) tile;
                CompoundNBT nbt = new CompoundNBT();
                spawner.getSpawnerBaseLogic().write(nbt);
                nbt.remove("SpawnPotentials");
                nbt.remove("SpawnData");
                nbt.put("SpawnData", itemstack.getTag().get(tagString).copy());
                spawner.getSpawnerBaseLogic().read(nbt);
                spawner.markDirty();
                ctx.getWorld().notifyBlockUpdate(ctx.getPos(), iblockstate, iblockstate, 3);
                return ActionResultType.SUCCESS;
            }
        }
        BlockPos blockpos;
        if (iblockstate.getCollisionShapeUncached(ctx.getWorld(), ctx.getPos()).isEmpty()) {
            blockpos = ctx.getPos();
        } else {
            blockpos = ctx.getPos().offset(ctx.getFace());
        }

        Entity entity = ItemExtendedSpawnEgg.spawnEntity((ServerWorld) ctx.getWorld(), itemstack, blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D);

        if (entity != null) {
            if (!ctx.getPlayer().abilities.isCreativeMode)
                itemstack.shrink(1);
            if (itemstack.hasDisplayName() && entity instanceof MobEntity) {
                Utils.updateEntity(itemstack.getDisplayName().getUnformattedComponentText(), (MobEntity) entity);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (world.isRemote || !itemstack.hasTag() || !itemstack.getTag().contains(MobBattle.MODID + ":Entity"))
            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
        else {
            BlockRayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.ANY);

            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = raytraceresult.getPos();

                if (!(world.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)) {
                    return new ActionResult<ItemStack>(ActionResultType.PASS, itemstack);
                } else if (world.isBlockModifiable(player, blockpos) && player.canPlayerEdit(blockpos, raytraceresult.getFace(), itemstack)) {
                    Entity entity = ItemExtendedSpawnEgg.spawnEntity((ServerWorld) world, itemstack, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
                            blockpos.getZ() + 0.5D);
                    if (entity != null) {
                        if (!player.abilities.isCreativeMode)
                            itemstack.shrink(1);
                        if (itemstack.hasDisplayName() && entity instanceof MobEntity) {
                            Utils.updateEntity(itemstack.getDisplayName().getUnformattedComponentText(), (MobEntity) entity);
                        }
                        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
                    }
                } else {
                    return new ActionResult<ItemStack>(ActionResultType.FAIL, itemstack);
                }
            }
        }
        return new ActionResult<ItemStack>(ActionResultType.PASS, itemstack);
    }

    public static Entity spawnEntity(ServerWorld world, ItemStack stack, double x, double y, double z) {
        Entity entity = null;
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            entity = EntityType.loadEntityAndExecute(stack.getTag().getCompound(tagString), world, Functions.identity());
            if (entity instanceof MobEntity) {
                MobEntity entityliving = (MobEntity) entity;
                /*
                 * if(CommonProxy.mca && entityliving instanceof EntityVillagerMCA &&
                 * stack.getTag().getCompound(tagString).contains("MCAGender")) { EntityVillagerMCA villager = (EntityVillagerMCA)
                 * entityliving;
                 * villager.attributes.setGender(EnumGender.byId(stack.getTag().getCompound(tagString).getInt("MCAGender")));
                 * villager.attributes.assignRandomName(); villager.attributes.assignRandomProfession();
                 * villager.attributes.assignRandomPersonality(); villager.attributes.assignRandomSkin(); }
                 */
                entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
                entityliving.rotationYawHead = entityliving.rotationYaw;
                entityliving.renderYawOffset = entityliving.rotationYaw;
                entityliving.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entityliving.getPositionVec())), SpawnReason.SPAWN_EGG, null, null);
                world.addEntity(entity);
                entityliving.playAmbientSound();
            }
        }
        return entity;
    }

    private static boolean hasSavedEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(tagString) && stack.getTag().getCompound(tagString).contains("id");
    }

    private void removeMobSpecifigTags(CompoundNBT compound) {
        compound.remove("Pos");
        compound.remove("Motion");
        compound.remove("Rotation");
        compound.remove("UUID");
        //Vanilla-fix incompability
        compound.remove("VFAABB");
    }

    @Nullable
    public static ResourceLocation getNamedIdFrom(ItemStack stack) {
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            String s = stack.getTag().getCompound(tagString).getString("id");

            ResourceLocation resourcelocation = new ResourceLocation(s);
            //fixing missing prefix case
            if (!s.contains(":")) {
                stack.getTag().getCompound(tagString).putString("id", resourcelocation.toString());
            }
            return resourcelocation;
        }
        return null;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return ItemExtendedSpawnEgg.hasSavedEntity(stack);
    }
}
