package io.github.flemmli97.mobbattle.forge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.forge.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MobBattle.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Item> reg : ModItems.ITEMS.getEntries()) {
            if (reg == ModItems.spawner) {
                this.withExistingParent(reg.getId().toString(), ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
            } else
                this.withExistingParent(reg.getId().toString(), ModelLocationUtils.decorateItemModelLocation("handheld"))
                        .texture("layer0", new ResourceLocation(reg.getId().getNamespace(), "items/" + reg.getId().getPath()));
        }
    }
}