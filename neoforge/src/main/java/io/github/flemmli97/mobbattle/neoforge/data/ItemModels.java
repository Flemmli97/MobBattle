package io.github.flemmli97.mobbattle.neoforge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.BossBarItemColor;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.neoforge.registry.Registers;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemModels extends ModelProvider {

    public ItemModels(PackOutput output) {
        super(output, MobBattle.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (DeferredHolder<Item, ?> reg : Registers.ITEMS.getEntries()) {
            if (reg == MobBattleItems.EXTENDED_EGG) {
                itemModels.itemModelOutput.accept(reg.get(), ItemModelUtils.plainModel(ModelTemplates.FLAT_HANDHELD_ITEM.create(
                        ModelLocationUtils.getModelLocation(reg.get()),
                        TextureMapping.layer0(new Material(Identifier.fromNamespaceAndPath(reg.getId().getNamespace(), "item/blank_spawn_egg"))),
                        itemModels.modelOutput)));
            } else if (reg == MobBattleItems.BOSS_BAR_ADDER) {
                itemModels.itemModelOutput.accept(reg.get(), ItemModelUtils.tintedModel(ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1).create(
                        ModelLocationUtils.getModelLocation(reg.get()),
                        TextureMapping.layered(new Material(Identifier.fromNamespaceAndPath(reg.getId().getNamespace(), "item/" + reg.getId().getPath())),
                                new Material(Identifier.fromNamespaceAndPath(reg.getId().getNamespace(), "item/" + reg.getId().getPath() + "_overlay"))),
                        itemModels.modelOutput), ItemModelGenerators.BLANK_LAYER, BossBarItemColor.INSTANCE));
            } else
                itemModels.generateFlatItem(reg.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        }
    }
}