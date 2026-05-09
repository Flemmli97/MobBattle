package io.github.flemmli97.mobbattle.fabric;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.EventCalls;
import io.github.flemmli97.mobbattle.common.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.common.registry.MobBattleMenuTypes;
import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import io.github.flemmli97.mobbattle.fabric.platform.CrossPlatformStuffImpl;
import io.github.flemmli97.mobbattle.network.C2SEffectStack;
import io.github.flemmli97.mobbattle.network.C2SItemFunctionPress;
import io.github.flemmli97.mobbattle.network.C2SSpawnEgg;
import io.github.flemmli97.mobbattle.network.S2CSpawnEggScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class MobBattleFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MobBattleItems.init();
        MobBattleDataComponents.init();
        MobBattleMenuTypes.init();
        postItemSetup();
        AttackEntityCallback.EVENT.register(EventHandler::attackCallback);
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            if (level.dimension().equals(Level.OVERWORLD)) {
                EventCalls.levelTick(level);
            }
        });
        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity instanceof LivingEntity living) {
                EventCalls.onStartTracking(player, living);
            }
        });
        EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
            if (entity instanceof LivingEntity living) {
                EventCalls.onStopTracking(player, living);
            }
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> EventCalls.handleJoinLevel(entity));
        registerPackets();
        ConfigLoader.initConfig();
        MobBattle.tenshiLib = FabricLoader.getInstance().isModLoaded("tenshilib");
        Identifier tab = MobBattle.of("tab");
        CreativeModeTab creativeModeTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tab, FabricCreativeModeTab.builder()
                .title(Component.translatable("mobbattle.tab"))
                .icon(() -> new ItemStack(MobBattleItems.MOB_STICK.get()))
                .build());
        MobBattle.customTab = () -> creativeModeTab;
        CreativeModeTabEvents.modifyOutputEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, tab)).register(content ->
                CrossPlatformStuffImpl.modItems().forEach(content::accept));
    }

    public static void postItemSetup() {
        DispenserBlock.registerBehavior(MobBattleItems.EXTENDED_EGG.get(), (source, stack) -> {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            double x = source.center().x() + direction.getStepX();
            double y = source.pos().getY() + direction.getStepY() + 0.2;
            double z = source.center().z() + direction.getStepZ();
            BlockPos blockpos = BlockPos.containing(x, y, z);
            boolean spawned = ItemExtendedSpawnEgg.spawnEntity(source.level(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D, direction);
            if (spawned) {
                stack.shrink(1);
            }
            return stack;
        });
    }

    public static void registerPackets() {
        PayloadTypeRegistry.serverboundPlay().register(C2SEffectStack.TYPE, C2SEffectStack.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SEffectStack.TYPE, (pkt, ctx) -> C2SEffectStack.handle(pkt, ctx.player(), MobBattleItems.MOB_EFFECT_GIVE.get()));
        PayloadTypeRegistry.serverboundPlay().register(C2SSpawnEgg.TYPE, C2SSpawnEgg.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SSpawnEgg.TYPE, (pkt, ctx) -> C2SSpawnEgg.handle(pkt, ctx.player()));
        PayloadTypeRegistry.serverboundPlay().register(C2SItemFunctionPress.TYPE, C2SItemFunctionPress.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SItemFunctionPress.TYPE, (pkt, ctx) -> C2SItemFunctionPress.handle(pkt, ctx.player()));

        PayloadTypeRegistry.clientboundPlay().register(S2CSpawnEggScreen.TYPE, S2CSpawnEggScreen.STREAM_CODEC);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(S2CSpawnEggScreen.TYPE, (pkt, ctx) -> S2CSpawnEggScreen.handle(pkt));
        }
    }
}
