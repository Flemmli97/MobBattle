package io.github.flemmli97.mobbattle.items;

import com.google.common.base.Functions;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemExtendedSpawnEgg extends Item implements LeftClickInteractItem {

    public ItemExtendedSpawnEgg(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> list, TooltipFlag flagIn) {
        list.add(new TranslatableComponent("tooltip.spawnegg").withStyle(ChatFormatting.AQUA));
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            CompoundTag compound = stack.getTag().getCompound(LibTags.spawnEggTag);
            String entity = EntityType.byString(compound.getString("id")).isPresent()
                    ? EntityType.byString(compound.getString("id")).get().getDescriptionId()
                    : "";
            if (!entity.isEmpty()) {
                String entityName = compound.contains("CustomName") ? compound.getString("CustomName") : I18n.get(entity);
                list.add(new TranslatableComponent("tooltip.spawnegg.spawn", entityName + (compound.size() > 1 ? " (+NBT)" : "")).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Mob e) {
            boolean nbt = false;
            CompoundTag compound = stack.getTag();
            if (compound == null)
                compound = new CompoundTag();
            CompoundTag tag = new CompoundTag();
            if (player.isShiftKeyDown()) {
                e.save(tag);
                this.removeMobSpecifigTags(tag);
                nbt = true;
            } else {
                String name = CrossPlatformStuff.instance().registryEntities().getIDFrom(e.getType()).toString();
                if (name != null)
                    tag.putString("id", name);
            }
            compound.put(LibTags.spawnEggTag, tag);
            stack.setTag(compound);

            if (!player.level.isClientSide) {
                player.sendMessage(new TranslatableComponent("tooltip.spawnegg.save", (nbt ? " + nbt" : "")).withStyle(ChatFormatting.GOLD), player.getUUID());
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide)
            return InteractionResult.PASS;
        ItemStack itemstack = ctx.getItemInHand();
        if (!ctx.getPlayer().mayUseItemAt(ctx.getClickedPos().relative(ctx.getClickedFace()), ctx.getClickedFace(), itemstack))
            return InteractionResult.PASS;
        BlockState iblockstate = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (hasSavedEntity(itemstack)) {
            BlockEntity tile = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
            if (tile instanceof SpawnerBlockEntity spawner) {
                CompoundTag nbt = new CompoundTag();
                spawner.getSpawner().save(nbt);
                nbt.remove("SpawnPotentials");
                nbt.remove("SpawnData");
                nbt.put("SpawnData", itemstack.getTag().get(LibTags.spawnEggTag).copy());
                spawner.getSpawner().load(tile.getLevel(), tile.getBlockPos(), nbt);
                spawner.setChanged();
                ctx.getLevel().sendBlockUpdated(ctx.getClickedPos(), iblockstate, iblockstate, 3);
                return InteractionResult.SUCCESS;
            }
        }
        BlockPos blockpos;
        if (iblockstate.getCollisionShape(ctx.getLevel(), ctx.getClickedPos()).isEmpty()) {
            blockpos = ctx.getClickedPos();
        } else {
            blockpos = ctx.getClickedPos().relative(ctx.getClickedFace());
        }

        Entity entity = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) ctx.getLevel(), itemstack, blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D);

        if (entity != null) {
            if (!ctx.getPlayer().getAbilities().instabuild)
                itemstack.shrink(1);
            if (itemstack.hasCustomHoverName() && entity instanceof Mob) {
                Utils.updateEntity(itemstack.getHoverName().getContents(), (Mob) entity);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (world.isClientSide || !itemstack.hasTag() || !itemstack.getTag().contains(MobBattle.MODID + ":Entity"))
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        else {
            BlockHitResult raytraceresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);

            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(world.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                    return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
                } else if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, raytraceresult.getDirection(), itemstack)) {
                    Entity entity = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) world, itemstack, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
                            blockpos.getZ() + 0.5D);
                    if (entity != null) {
                        if (!player.getAbilities().instabuild)
                            itemstack.shrink(1);
                        if (itemstack.hasCustomHoverName() && entity instanceof Mob) {
                            Utils.updateEntity(itemstack.getHoverName().getContents(), (Mob) entity);
                        }
                        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
                    }
                } else {
                    return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
                }
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    public static Entity spawnEntity(ServerLevel world, ItemStack stack, double x, double y, double z) {
        Entity entity = null;
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            CompoundTag tag = stack.getTag().getCompound(LibTags.spawnEggTag);
            entity = EntityType.loadEntityRecursive(tag, world, Functions.identity());
            if (entity instanceof Mob entityliving) {
                entity.moveTo(x, y, z, Mth.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
                entityliving.yHeadRot = entityliving.getYRot();
                entityliving.yBodyRot = entityliving.getYRot();
                if (tag.size() == 1)
                    entityliving.finalizeSpawn(world, world.getCurrentDifficultyAt(new BlockPos(entityliving.position())), MobSpawnType.SPAWN_EGG, null, null);
                world.addFreshEntity(entity);
                entityliving.playAmbientSound();
            }
        }
        return entity;
    }

    private static boolean hasSavedEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(LibTags.spawnEggTag) && stack.getTag().getCompound(LibTags.spawnEggTag).contains("id");
    }

    private void removeMobSpecifigTags(CompoundTag compound) {
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
            String s = stack.getTag().getCompound(LibTags.spawnEggTag).getString("id");

            ResourceLocation resourcelocation = new ResourceLocation(s);
            //fixing missing prefix case
            if (!s.contains(":")) {
                stack.getTag().getCompound(LibTags.spawnEggTag).putString("id", resourcelocation.toString());
            }
            return resourcelocation;
        }
        return null;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ItemExtendedSpawnEgg.hasSavedEntity(stack);
    }
}
