package io.github.flemmli97.mobbattle.neoforge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.data.Lang;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MobBattle.MODID)
public class DataEvent {

    @SubscribeEvent
    public static void data(GatherDataEvent.Client event) {
        DataGenerator data = event.getGenerator();
        data.addProvider(true, new Lang(data.getPackOutput()));
        data.addProvider(true, new ItemModels(data.getPackOutput()));
        data.addProvider(true, new EntityTagGen(data.getPackOutput(), event.getLookupProvider()));
    }
}
