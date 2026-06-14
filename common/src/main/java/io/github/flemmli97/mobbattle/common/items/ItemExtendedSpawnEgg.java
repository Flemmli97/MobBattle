package io.github.flemmli97.mobbattle.common.items;

import com.mojang.logging.LogUtils;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.network.S2CSpawnEggScreen;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import io.github.flemmli97.tenshilib.common.item.SpawnEgg;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemExtendedSpawnEgg extends Item implements ExtendedItem {

    private static final Logger LOGGER = LogUtils.getLogger();

    public ItemExtendedSpawnEgg(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.mobbattle.spawnegg").withStyle(ChatFormatting.AQUA));
        Optional<EntityType<?>> entityType = ItemExtendedSpawnEgg.getType(stack);
        entityType.ifPresent(type -> {
            TypedEntityData<EntityType<?>> data = stack.get(DataComponents.ENTITY_DATA);
            adder.accept(Component.translatable("tooltip.mobbattle.spawnegg.spawn" + (!data.copyTagWithoutId().isEmpty() ? ".nbt" : ""), type.getDescription()).withStyle(ChatFormatting.GOLD));
        });
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetLivingEntity(entity);
        if (living instanceof Mob) {
            if (!player.isCreative()) {
                player.sendSystemMessage(Component.translatable("tooltip.mobbattle.spawnegg.creative").withStyle(ChatFormatting.GOLD));
                return true;
            }
            boolean nbt = false;
            CompoundTag tag = new CompoundTag();
            if (player.isShiftKeyDown()) {
                try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
                    TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
                    entity.save(output);
                    tag = output.buildResult();
                }
                this.removeMobSpecificTags(tag);
                nbt = true;
            }
            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(living.getType(), tag));
            Identifier model = SpawnEggItem.byId(living.getType()).flatMap(Holder::unwrapKey)
                    .map(ResourceKey::identifier).orElse(null);
            if (model == null && MobBattle.tenshiLib) {
                model = SpawnEgg.fromType(living.getType()).map(BuiltInRegistries.ITEM::getKey).orElse(null);
            }
            if (model == null)
                model = BuiltInRegistries.ITEM.getKey(this);
            stack.set(DataComponents.ITEM_MODEL, model);
            if (!player.level().isClientSide()) {
                player.sendSystemMessage(Component.translatable("tooltip.mobbattle.spawnegg.save" + (nbt ? ".nbt" : ""), living.getName()).withStyle(ChatFormatting.GOLD));
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!ctx.getPlayer().mayUseItemAt(ctx.getClickedPos().relative(ctx.getClickedFace()), ctx.getClickedFace(), ctx.getItemInHand()))
            return InteractionResult.PASS;
        if (!(ctx.getLevel() instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;
        ItemStack stack = ctx.getItemInHand();
        Optional<EntityType<?>> entityType = ItemExtendedSpawnEgg.getType(stack);
        if (entityType.isEmpty())
            return InteractionResult.PASS;
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        BlockEntity blockEntity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
        if (blockEntity instanceof SpawnerBlockEntity spawner) {
            CompoundTag nbt;
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(spawner.problemPath(), LOGGER)) {
                TagValueOutput output = TagValueOutput.createWithContext(reporter, spawner.getLevel().registryAccess());
                spawner.getSpawner().save(output);
                nbt = output.buildResult();

                nbt.remove("SpawnPotentials");
                nbt.remove(BaseSpawner.SPAWN_DATA_TAG);
                SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, new SpawnData(stack.get(DataComponents.ENTITY_DATA).copyTagWithoutId(), Optional.empty(), Optional.empty()))
                        .resultOrPartial(string -> MobBattle.LOGGER.warn("Invalid SpawnData: {}", string))
                        .ifPresent(t -> nbt.put(BaseSpawner.SPAWN_DATA_TAG, t));
                spawner.getSpawner().load(blockEntity.getLevel(), blockEntity.getBlockPos(), TagValueInput.create(reporter, spawner.getLevel().registryAccess(), nbt));
                spawner.setEntityId(entityType.get(), serverLevel.getRandom());
            }
            spawner.setChanged();
            ctx.getLevel().sendBlockUpdated(ctx.getClickedPos(), state, state, 3);
            return InteractionResult.CONSUME;
        }
        BlockPos blockpos;
        if (state.getCollisionShape(ctx.getLevel(), ctx.getClickedPos()).isEmpty()) {
            blockpos = ctx.getClickedPos();
        } else {
            blockpos = ctx.getClickedPos().relative(ctx.getClickedFace());
        }
        boolean spawned = ItemExtendedSpawnEgg.spawnEntity(serverLevel, entityType.get(), ctx.getPlayer(), stack,
                blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, ctx.getHorizontalDirection());
        if (spawned && !ctx.getPlayer().getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer))
            return InteractionResult.SUCCESS;
        Optional<EntityType<?>> entityType = ItemExtendedSpawnEgg.getType(stack);
        if (entityType.isEmpty())
            return InteractionResult.PASS;
        BlockHitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (raytraceresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = raytraceresult.getBlockPos();
            if (!(level.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResult.PASS;
            } else if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, raytraceresult.getDirection(), stack)) {
                boolean spawned = ItemExtendedSpawnEgg.spawnEntity(serverPlayer.level(), entityType.get(), serverPlayer, stack,
                        blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, player.getDirection());
                if (spawned && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        } else {
            CrossPlatformStuff.INSTANCE.sendToClient(new S2CSpawnEggScreen(hand, S2CSpawnEggScreen.ScreenType.SPAWN_EGG), serverPlayer);
        }
        return InteractionResult.CONSUME;
    }

    public static boolean spawnEntity(ServerLevel level, ItemStack stack, double x, double y, double z, Direction direction) {
        return getType(stack).map(type -> spawnEntity(level, type, null, stack, x, y, z, direction)).orElse(false);
    }

    private static boolean spawnEntity(ServerLevel level, EntityType<?> type, LivingEntity user, ItemStack stack, double x, double y, double z, Direction direction) {
        SpawnEggOptions options = stack.getOrDefault(MobBattleDataComponents.SPAWN_EGG_OPTIONS.get(), SpawnEggOptions.DEFAULT);
        boolean success = false;
        int sqr = options.amount() > 1 ? (int) Math.ceil(Math.sqrt(options.amount())) : 0;
        BlockPos origin = BlockPos.containing(x, y, z);
        for (int i = 0; i < options.amount(); i++) {
            Entity entity = getEntity(level, type, user, stack, origin);
            if (entity instanceof Mob mob) {
                if (options.amount() > 1 && options.spacing() > 0) {
                    int dL = i / sqr;
                    int dW = i % sqr - sqr / 2;
                    Vec3i front = direction.getUnitVec3i().multiply(options.spacing());
                    Vec3i side = new Vec3i(front.getZ(), front.getY(), -front.getX());
                    entity.moveOrInterpolateTo(new Vec3(x + side.getX() * dW + front.getX() * dL, y, z + side.getZ() * dW + front.getZ() * dL),
                            Mth.wrapDegrees(direction.toYRot() - 180), 0.0F);
                } else {
                    entity.moveOrInterpolateTo(new Vec3(x, y, z), Mth.wrapDegrees(level.getRandom().nextFloat() * 360.0F), 0.0F);
                }
                mob.yHeadRot = mob.getYRot();
                mob.yBodyRot = mob.getYRot();
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
        return getType(stack).map(type -> getEntity(level, type, null, stack, null)).orElse(null);
    }

    private static Entity getEntity(Level level, EntityType<?> type, LivingEntity user, ItemStack stack, @Nullable BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            return type.create(serverLevel, EntityType.createDefaultStackConfig(level, stack, user), pos == null ? BlockPos.ZERO : pos, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
        }
        TypedEntityData<EntityType<?>> data = stack.get(DataComponents.ENTITY_DATA);
        CompoundTag tag = data == null ? new CompoundTag() : data.copyTagWithoutId();
        tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        return EntityType.loadEntityRecursive(tag, level, EntitySpawnReason.SPAWN_ITEM_USE, EntityProcessor.NOP);
    }

    public static Optional<EntityType<?>> getType(ItemStack stack) {
        TypedEntityData<EntityType<?>> data = stack.get(DataComponents.ENTITY_DATA);
        return Optional.ofNullable(data != null ? data.type() : null);
    }

    private void removeMobSpecificTags(CompoundTag compound) {
        compound.remove("Pos");
        compound.remove("Motion");
        compound.remove("Rotation");
        compound.remove("UUID");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ItemExtendedSpawnEgg.getType(stack).isPresent();
    }
}
