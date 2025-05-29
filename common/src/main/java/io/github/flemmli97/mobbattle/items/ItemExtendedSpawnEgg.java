package io.github.flemmli97.mobbattle.items;

import com.google.common.base.Functions;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.network.S2CSpawnEggScreen;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ItemExtendedSpawnEgg extends Item implements LeftClickInteractItem {

    public ItemExtendedSpawnEgg(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> list, TooltipFlag flagIn) {
        list.add(Component.translatable("tooltip.spawnegg").withStyle(ChatFormatting.AQUA));
        ResourceLocation id = getNamedIdFrom(stack);
        if (id != null) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            CompoundTag compound = stack.getTag().getCompound(LibTags.SPAWN_EGG_TAG);
            list.add(Component.translatable("tooltip.spawnegg.spawn" + (compound.size() > 1 ? ".nbt" : ""), type.getDescription()).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living instanceof Mob mob) {
            if (!player.isCreative()) {
                player.sendSystemMessage(Component.translatable("tooltip.spawnegg.creative").withStyle(ChatFormatting.GOLD));
                return true;
            }
            boolean nbt = false;
            CompoundTag compound = stack.getTag();
            if (compound == null)
                compound = new CompoundTag();
            CompoundTag tag = new CompoundTag();
            if (player.isShiftKeyDown()) {
                mob.save(tag);
                this.removeMobSpecifigTags(tag);
                nbt = true;
            } else {
                String name = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
                if (name != null)
                    tag.putString("id", name);
            }
            compound.put(LibTags.SPAWN_EGG_TAG, tag);
            stack.setTag(compound);

            if (!player.level().isClientSide) {
                player.sendSystemMessage(Component.translatable("tooltip.spawnegg.save" + (nbt ? ".nbt" : ""), mob.getName()).withStyle(ChatFormatting.GOLD));
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
                nbt.remove(BaseSpawner.SPAWN_DATA_TAG);
                SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, new SpawnData(itemstack.getTag().getCompound(LibTags.SPAWN_EGG_TAG), Optional.empty()))
                        .resultOrPartial(string -> MobBattle.LOGGER.warn("Invalid SpawnData: {}", string))
                        .ifPresent(t -> nbt.put(BaseSpawner.SPAWN_DATA_TAG, t));
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

        boolean spawned = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) ctx.getLevel(), itemstack,
                blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, ctx.getHorizontalDirection());
        if (spawned && !ctx.getPlayer().getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!itemstack.hasTag() || !itemstack.getTag().contains(MobBattle.MODID + ":Entity"))
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        if (player instanceof ServerPlayer serverPlayer) {
            BlockHitResult raytraceresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(world.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                    return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
                } else if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, raytraceresult.getDirection(), itemstack)) {
                    boolean spawned = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) world, itemstack, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
                            blockpos.getZ() + 0.5D, player.getDirection());
                    if (spawned && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                }
            } else {
                CrossPlatformStuff.INSTANCE.sendToClient(new S2CSpawnEggScreen(hand), serverPlayer);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemstack, player.level().isClientSide);
    }

    public static boolean spawnEntity(ServerLevel level, ItemStack stack, double x, double y, double z, Direction direction) {
        SpawnOptions options = getOptions(stack);
        boolean success = false;
        int sqr = options.amount() > 1 ? (int) Math.ceil(Math.sqrt(options.amount())) : 0;
        for (int i = 0; i < options.amount(); i++) {
            Entity entity = getEntity(level, stack);
            if (entity instanceof Mob mob) {
                CompoundTag tag = stack.getTag().getCompound(LibTags.SPAWN_EGG_TAG);
                if (options.amount() > 1 && options.spacing() > 0) {
                    int dL = i / sqr;
                    int dW = i % sqr - sqr / 2;
                    Vec3i front = direction.getNormal().multiply(options.spacing());
                    Vec3i side = new Vec3i(front.getZ(), front.getY(), -front.getX());
                    entity.moveTo(x + side.getX() * dW + front.getX() * dL, y, z + side.getZ() * dW + front.getZ() * dL,
                            Mth.wrapDegrees(direction.toYRot() - 180), 0.0F);
                } else {
                    entity.moveTo(x, y, z, Mth.wrapDegrees(level.random.nextFloat() * 360.0F), 0.0F);
                }
                mob.yHeadRot = mob.getYRot();
                mob.yBodyRot = mob.getYRot();
                if (tag.size() == 1)
                    mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(mob.position())), MobSpawnType.SPAWN_EGG, null, null);
                level.addFreshEntity(entity);
                mob.playAmbientSound();
                if (options.team() != null && !options.team().isEmpty()) {
                    Utils.updateEntity(options.team(), mob);
                }
                if (stack.hasCustomHoverName()) {
                    entity.setCustomName(stack.getHoverName());
                }
                success = true;
            }
        }
        return success;
    }

    public static Entity getEntity(Level level, ItemStack stack) {
        Entity entity = null;
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            CompoundTag tag = stack.getTag().getCompound(LibTags.SPAWN_EGG_TAG);
            entity = EntityType.loadEntityRecursive(tag, level, Functions.identity());
        }
        return entity;
    }

    public static SpawnOptions getOptions(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag = tag.getCompound(LibTags.SPAWN_EGG_OPTIONS);
            return new SpawnOptions(tag.contains("Team") ? tag.getString("Team") : null,
                    Math.max(1, tag.getInt("Amount")), Math.max(0, tag.getInt("Spacing")));
        }
        return new SpawnOptions(null, 1, 0);
    }

    public static void updateOptions(ItemStack stack, SpawnOptions options) {
        CompoundTag tag = stack.getTag();
        if (tag == null)
            tag = new CompoundTag();
        CompoundTag optionsTag = new CompoundTag();
        if (options.team() != null && !options.team().isEmpty()) {
            optionsTag.putString("Team", options.team());
        }
        if (options.amount() != 1) {
            optionsTag.putInt("Amount", options.amount());
        }
        if (options.spacing() != 0) {
            optionsTag.putInt("Spacing", options.spacing());
        }
        if (!optionsTag.isEmpty()) {
            tag.put(LibTags.SPAWN_EGG_OPTIONS, optionsTag);
            stack.setTag(tag);
        }
    }

    private static boolean hasSavedEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(LibTags.SPAWN_EGG_TAG) && stack.getTag().getCompound(LibTags.SPAWN_EGG_TAG).contains("id");
    }

    private void removeMobSpecifigTags(CompoundTag compound) {
        compound.remove("Pos");
        compound.remove("Motion");
        compound.remove("Rotation");
        compound.remove("UUID");
    }

    @Nullable
    public static ResourceLocation getNamedIdFrom(ItemStack stack) {
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            String s = stack.getTag().getCompound(LibTags.SPAWN_EGG_TAG).getString("id");

            ResourceLocation resourcelocation = new ResourceLocation(s);
            //fixing missing prefix case
            if (!s.contains(":")) {
                stack.getTag().getCompound(LibTags.SPAWN_EGG_TAG).putString("id", resourcelocation.toString());
            }
            return resourcelocation;
        }
        return null;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ItemExtendedSpawnEgg.hasSavedEntity(stack);
    }

    public record SpawnOptions(@Nullable String team, int amount, int spacing) {

        public SpawnOptions(@Nullable String team, int amount, int spacing) {
            this.team = team;
            this.amount = Mth.clamp(amount, 0, 100);
            this.spacing = Mth.clamp(spacing, 0, 99);
        }
    }
}
