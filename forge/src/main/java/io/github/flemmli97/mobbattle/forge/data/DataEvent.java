package io.github.flemmli97.mobbattle.forge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.data.Lang;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = MobBattle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataEvent {

    @SubscribeEvent
    public static void data(GatherDataEvent event) {
        DataGenerator data = event.getGenerator();
        if (event.includeClient()) {
            data.addProvider(new Lang(data));
            data.addProvider(new ItemModels(data, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            data.addProvider(new EntityTagGen(data, event.getExistingFileHelper()));
        }
    }

}
