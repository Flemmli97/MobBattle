package io.github.flemmli97.mobbattle.neoforge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.neoforge.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemModels extends ModelProvider {

    public ItemModels(PackOutput output) {
        super(output, MobBattle.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (DeferredHolder<Item, ? extends Item> reg : ModItems.ITEMS.getEntries()) {
            if (reg == ModItems.EXTENDED_EGG) {
                itemModels.itemModelOutput.accept(reg.get(), ItemModelUtils.plainModel(ModelTemplates.FLAT_HANDHELD_ITEM.create(
                        ModelLocationUtils.getModelLocation(reg.get()),
                        TextureMapping.layer0(ResourceLocation.fromNamespaceAndPath(reg.getId().getNamespace(), "item/blank_spawn_egg")),
                        itemModels.modelOutput)));
            } else
                itemModels.generateFlatItem(reg.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        }
    }
}