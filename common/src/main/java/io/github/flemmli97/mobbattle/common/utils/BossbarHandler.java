package io.github.flemmli97.mobbattle.common.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossbarHandler extends SavedData {

    private static final String IDENTIFIER = "MobBattleBossBars";
    private static final SavedData.Factory<BossbarHandler> FACTORY = new Factory<>(BossbarHandler::new, BossbarHandler::new, DataFixTypes.LEVEL);

    private final Map<UUID, MobBossEvent> bars = new HashMap<>();

    private BossbarHandler() {
    }

    private BossbarHandler(CompoundTag tag, HolderLookup.Provider provider) {
        this.load(tag, provider);
    }

    public static BossbarHandler get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, IDENTIFIER);
    }

    public void addBossBarTo(LivingEntity target, BossEvent.BossBarColor color) {
        MobBossEvent event = this.bars.get(target.getUUID());
        if (event == null) {
            event = new MobBossEvent(target);
            event.setColor(color);
            this.bars.put(event.id(), event);
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

    public void load(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag bossBars = tag.getList("BossBars", Tag.TAG_COMPOUND);
        bossBars.forEach(t -> {
            MobBossEvent evt = MobBossEvent.load((CompoundTag) t, provider);
            this.bars.put(evt.id(), evt);
        });
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag bossBars = new ListTag();
        this.bars.forEach((id, evt) -> bossBars.add(evt.save(provider)));
        tag.put("BossBars", bossBars);
        return tag;
    }
}
