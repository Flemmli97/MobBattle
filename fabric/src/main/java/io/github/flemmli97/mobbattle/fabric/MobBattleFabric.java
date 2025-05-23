package io.github.flemmli97.mobbattle.fabric;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import io.github.flemmli97.mobbattle.fabric.registry.ModItems;
import io.github.flemmli97.mobbattle.fabric.registry.ModMenuType;
import io.github.flemmli97.mobbattle.network.PacketRegistrar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobBattleFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModMenuType.register();
        AttackEntityCallback.EVENT.register(EventHandler::attackCallback);
        PacketRegistrar.registerServerPackets(new PacketRegistrar.ServerPacketRegister() {
            @Override
            public <P> void registerMessage(int index, ResourceLocation id, Class<P> clss, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder, BiConsumer<P, ServerPlayer> handler) {
                ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler1, buf, responseSender) -> {
                    P pkt = decoder.apply(buf);
                    server.execute(() -> handler.accept(pkt, player));
                });
            }
        }, 0);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            PacketRegistrar.registerClientPackets(new PacketRegistrar.ClientPacketRegister() {
                @Override
                public <P> void registerMessage(int index, ResourceLocation id, Class<P> clss, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder, Consumer<P> handler) {
                    ClientPlayNetworking.registerGlobalReceiver(id, (client, handler1, buf, responseSender) -> {
                        P pkt = decoder.apply(buf);
                        client.execute(() -> handler.accept(pkt));
                    });
                }
            }, 0);
        }
        Config.initConfig();
        MobBattle.tenshiLib = FabricLoader.getInstance().isModLoaded("tenshilib");
        ResourceLocation tab = new ResourceLocation("mobbattle", "tab");
        CreativeModeTab creativeModeTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tab, FabricItemGroup.builder()
                .title(Component.translatable("mobbattle.tab"))
                .icon(() -> new ItemStack(ModItems.mobStick))
                .build());
        MobBattle.customTab = () -> creativeModeTab;
        ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, tab)).register(content -> {
            ModItems.modItems().forEach(content::accept);
        });
    }
}
