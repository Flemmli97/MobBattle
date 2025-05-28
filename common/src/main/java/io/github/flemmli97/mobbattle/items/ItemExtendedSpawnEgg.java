package io.github.flemmli97.mobbattle.items;

import com.google.common.base.Functions;
import com.mojang.serialization.MapCodec;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.network.S2CSpawnEggScreen;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import io.github.flemmli97.tenshilib.common.item.SpawnEgg;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
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

import java.util.Optional;
import java.util.function.Consumer;

public class ItemExtendedSpawnEgg extends Item implements LeftClickInteractItem {

    private static final MapCodec<ResourceLocation> ENTITY_TYPE_ID_CODEC = ResourceLocation.CODEC.fieldOf("id");

    public ItemExtendedSpawnEgg(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.spawnegg").withStyle(ChatFormatting.AQUA));
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            CustomData data = stack.get(DataComponents.ENTITY_DATA);
            BuiltInRegistries.ENTITY_TYPE.getOptional(data.read(ENTITY_TYPE_ID_CODEC).result().get())
                    .ifPresent(type -> {
                        adder.accept(Component.translatable("tooltip.spawnegg.spawn" + (data.size() > 1 ? ".nbt" : ""), type.getDescription()).withStyle(ChatFormatting.GOLD));
                    });
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Mob) {
            if (player instanceof ServerPlayer serverPlayer) {
                boolean nbt = false;
                CompoundTag tag = new CompoundTag();
                if (player.isShiftKeyDown()) {
                    entity.save(tag);
                    this.removeMobSpecificTags(tag);
                    nbt = true;
                } else {
                    String name = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
                    tag.putString("id", name);
                }
                stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
                ResourceLocation model = null;
                SpawnEggItem vanillaEgg = SpawnEggItem.byId(entity.getType());
                if (vanillaEgg != null)
                    model = BuiltInRegistries.ITEM.getKey(vanillaEgg);
                else if (MobBattle.tenshiLib) {
                    model = SpawnEgg.fromType(entity.getType()).map(BuiltInRegistries.ITEM::getKey).orElse(null);
                }
                if (model != null)
                    stack.set(DataComponents.ITEM_MODEL, model);

                serverPlayer.sendSystemMessage(Component.translatable("tooltip.spawnegg.save" + (nbt ? ".nbt" : ""), entity.getName()).withStyle(ChatFormatting.GOLD));
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!ctx.getPlayer().mayUseItemAt(ctx.getClickedPos().relative(ctx.getClickedFace()), ctx.getClickedFace(), ctx.getItemInHand()))
            return InteractionResult.PASS;
        if (ctx.getLevel().isClientSide)
            return InteractionResult.SUCCESS;
        ItemStack stack = ctx.getItemInHand();
        BlockState iblockstate = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (hasSavedEntity(stack)) {
            BlockEntity tile = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
            if (tile instanceof SpawnerBlockEntity spawner) {
                CompoundTag nbt = new CompoundTag();
                spawner.getSpawner().save(nbt);
                nbt.remove("SpawnPotentials");
                nbt.remove(BaseSpawner.SPAWN_DATA_TAG);
                SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, new SpawnData(stack.get(DataComponents.ENTITY_DATA).copyTag(), Optional.empty(), Optional.empty()))
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

        boolean spawned = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) ctx.getLevel(), stack,
                blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, ctx.getHorizontalDirection());
        if (spawned && !ctx.getPlayer().getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!ItemExtendedSpawnEgg.hasSavedEntity(stack))
            return InteractionResult.PASS;
        if (player instanceof ServerPlayer serverPlayer) {
            BlockHitResult raytraceresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(world.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                    return InteractionResult.PASS;
                } else if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, raytraceresult.getDirection(), stack)) {
                    boolean spawned = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) world, stack, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
                            blockpos.getZ() + 0.5D, player.getDirection());
                    if (spawned && !player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
            } else {
                CrossPlatformStuff.INSTANCE.sendToClient(new S2CSpawnEggScreen(hand), serverPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean spawnEntity(ServerLevel level, ItemStack stack, double x, double y, double z, Direction direction) {
        SpawnEggOptions options = stack.getOrDefault(CrossPlatformStuff.INSTANCE.getComponentSpawnEggOptions(), SpawnEggOptions.DEFAULT);
        boolean success = false;
        int sqr = options.amount() > 1 ? (int) Math.ceil(Math.sqrt(options.amount())) : 0;
        for (int i = 0; i < options.amount(); i++) {
            Entity entity = getEntity(level, stack);
            if (entity instanceof Mob mob) {
                CompoundTag tag = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY).copyTag();
                if (options.amount() > 1 && options.spacing() > 0) {
                    int dL = i / sqr;
                    int dW = i % sqr - sqr / 2;
                    Vec3i front = direction.getUnitVec3i().multiply(options.spacing());
                    Vec3i side = new Vec3i(front.getZ(), front.getY(), -front.getX());
                    entity.snapTo(x + side.getX() * dW + front.getX() * dL, y, z + side.getZ() * dW + front.getZ() * dL,
                            Mth.wrapDegrees(direction.toYRot() - 180), 0.0F);
                } else {
                    entity.snapTo(x, y, z, Mth.wrapDegrees(level.random.nextFloat() * 360.0F), 0.0F);
                }
                mob.yHeadRot = mob.getYRot();
                mob.yBodyRot = mob.getYRot();
                if (tag.size() == 1)
                    mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(mob.position())), EntitySpawnReason.SPAWN_ITEM_USE, null);
                level.addFreshEntity(entity);
                mob.playAmbientSound();
                if (options.team() != null && !options.team().isEmpty()) {
                    Utils.updateEntity(options.team(), mob);
                }
                if (stack.has(DataComponents.CUSTOM_NAME)) {
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
            CompoundTag tag = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY).copyTag();
            entity = EntityType.loadEntityRecursive(tag, level, EntitySpawnReason.SPAWN_ITEM_USE, Functions.identity());
        }
        return entity;
    }

    private static boolean hasSavedEntity(ItemStack stack) {
        return stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY).read(ENTITY_TYPE_ID_CODEC).result().isPresent();
    }

    private void removeMobSpecificTags(CompoundTag compound) {
        compound.remove("Pos");
        compound.remove("Motion");
        compound.remove("Rotation");
        compound.remove("UUID");
    }

    @Nullable
    public static ResourceLocation getNamedIdFrom(ItemStack stack) {
        return stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY).read(ENTITY_TYPE_ID_CODEC).result().orElse(null);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ItemExtendedSpawnEgg.hasSavedEntity(stack);
    }
}
