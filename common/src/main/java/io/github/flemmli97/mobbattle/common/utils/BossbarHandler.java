package io.github.flemmli97.mobbattle.common.utils;

import com.mojang.serialization.Codec;
import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BossbarHandler extends SavedData {

    public static final Codec<BossbarHandler> CODEC = MobBossEvent.CODEC.listOf().fieldOf("BossBars")
            .xmap(BossbarHandler::new, h -> List.copyOf(h.bars.values())).codec();

    private static final SavedDataType<BossbarHandler> TYPE = new SavedDataType<>(MobBattle.of("boss_bars"), BossbarHandler::new, CODEC, DataFixTypes.LEVEL);

    private final Map<UUID, MobBossEvent> bars = new HashMap<>();

    private BossbarHandler() {
    }

    private BossbarHandler(List<MobBossEvent> bossBars) {
        bossBars.forEach(evt -> {
            this.bars.put(evt.getId(), evt);
        });
    }

    public static BossbarHandler get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public void addBossBarTo(LivingEntity target, BossEvent.BossBarColor color) {
        MobBossEvent event = this.bars.get(target.getUUID());
        if (event == null) {
            event = new MobBossEvent(target);
            event.setColor(color);
            this.bars.put(event.getId(), event);
            this.setDirty();
        }
        event.setColor(color);
    }

    public void removeBossbar(LivingEntity target) {
        MobBossEvent event = this.bars.remove(target.getUUID());
        if (event != null) {
            event.remove();
            this.setDirty();
        }
    }

    public void onMobLoad(LivingEntity entity) {
        MobBossEvent event = this.bars.get(entity.getUUID());
        if (event != null) {
            event.setEntity(entity);
        }
    }

    public void onStartTracking(ServerPlayer player, LivingEntity target) {
        MobBossEvent event = this.bars.get(target.getUUID());
        if (event != null) {
            event.addPlayer(player);
        }
    }

    public void onStopTracking(ServerPlayer player, LivingEntity target) {
        MobBossEvent event = this.bars.get(target.getUUID());
        if (event != null) {
            event.removePlayer(player);
        }
    }

    public void tick() {
        if (!this.bars.isEmpty())
            this.setDirty();
        this.bars.values().removeIf(MobBossEvent::tick);
    }
}
