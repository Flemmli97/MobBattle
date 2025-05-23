package io.github.flemmli97.mobbattle.neoforge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.data.Lang;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MobBattle.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataEvent {

    @SubscribeEvent
    public static void data(GatherDataEvent event) {
        DataGenerator data = event.getGenerator();
        data.addProvider(event.includeClient(), new Lang(data.getPackOutput()));
        data.addProvider(event.includeClient(), new ItemModels(data.getPackOutput(), event.getExistingFileHelper()));
        data.addProvider(event.includeServer(), new EntityTagGen(data.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
    }

}
