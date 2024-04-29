package io.github.flemmli97.mobbattle.items;

import com.google.common.base.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.handler.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.component.CustomData;
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

    private static final MapCodec<ResourceLocation> ENTITY_TYPE_ID_CODEC = ResourceLocation.CODEC.fieldOf("id");
    private static final MapCodec<String> NAME_GETTER_CODEC = Codec.STRING.fieldOf("CustomName");

    public ItemExtendedSpawnEgg(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("tooltip.spawnegg").withStyle(ChatFormatting.AQUA));
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            CustomData data = stack.get(DataComponents.ENTITY_DATA);
            Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(data.read(ENTITY_TYPE_ID_CODEC).result().get());
            String entity = type.map(EntityType::getDescriptionId).orElse("");
            if (!entity.isEmpty()) {
                String entityName = data.read(NAME_GETTER_CODEC).result().orElse(I18n.get(entity));
                list.add(Component.translatable("tooltip.spawnegg.spawn", entityName + (data.size() > 1 ? " (+NBT)" : "")).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Mob e) {
            boolean nbt = false;
            CompoundTag tag = new CompoundTag();
            if (player.isShiftKeyDown()) {
                e.save(tag);
                this.removeMobSpecificTags(tag);
                nbt = true;
            } else {
                String name = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString();
                tag.putString("id", name);
            }
            stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));

            if (!player.level().isClientSide) {
                player.sendSystemMessage(Component.translatable("tooltip.spawnegg.save", (nbt ? "+ nbt" : "")).withStyle(ChatFormatting.GOLD));
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide)
            return InteractionResult.PASS;
        ItemStack stack = ctx.getItemInHand();
        if (!ctx.getPlayer().mayUseItemAt(ctx.getClickedPos().relative(ctx.getClickedFace()), ctx.getClickedFace(), stack))
            return InteractionResult.PASS;
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

        Entity entity = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) ctx.getLevel(), stack, blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D);

        if (entity != null) {
            if (!ctx.getPlayer().getAbilities().instabuild)
                stack.shrink(1);
            Component component = stack.get(DataComponents.CUSTOM_NAME);
            if (component != null && entity instanceof Mob) {
                Utils.updateEntity(component.getString(), (Mob) entity);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide || !ItemExtendedSpawnEgg.hasSavedEntity(stack))
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        else {
            BlockHitResult raytraceresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);

            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(world.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                    return new InteractionResultHolder<>(InteractionResult.PASS, stack);
                } else if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, raytraceresult.getDirection(), stack)) {
                    Entity entity = ItemExtendedSpawnEgg.spawnEntity((ServerLevel) world, stack, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
                            blockpos.getZ() + 0.5D);
                    if (entity != null) {
                        if (!player.getAbilities().instabuild)
                            stack.shrink(1);
                        Component component = stack.get(DataComponents.CUSTOM_NAME);
                        if (component != null && entity instanceof Mob) {
                            Utils.updateEntity(component.getString(), (Mob) entity);
                        }
                        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
                    }
                } else {
                    return new InteractionResultHolder<>(InteractionResult.PASS, stack);
                }
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    public static Entity spawnEntity(ServerLevel world, ItemStack stack, double x, double y, double z) {
        Entity entity = null;
        if (ItemExtendedSpawnEgg.hasSavedEntity(stack)) {
            CompoundTag tag = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY).copyTag();
            entity = EntityType.loadEntityRecursive(tag, world, Functions.identity());
            if (entity instanceof Mob mob) {
                entity.moveTo(x, y, z, Mth.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
                mob.yHeadRot = mob.getYRot();
                mob.yBodyRot = mob.getYRot();
                if (tag.size() == 1)
                    mob.finalizeSpawn(world, world.getCurrentDifficultyAt(BlockPos.containing(mob.position())), MobSpawnType.SPAWN_EGG, null);
                world.addFreshEntity(entity);
                mob.playAmbientSound();
            }
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
