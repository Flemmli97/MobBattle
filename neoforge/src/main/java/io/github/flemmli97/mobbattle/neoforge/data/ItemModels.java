package io.github.flemmli97.mobbattle.neoforge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.neoforge.registry.Registers;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemModels extends ItemModelProvider {

    public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MobBattle.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (DeferredHolder<?, ?> reg : Registers.ITEMS.getEntries()) {
            if (reg == MobBattleItems.EXTENDED_EGG) {
                this.withExistingParent(reg.getId().toString(), ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
            } else
                this.withExistingParent(reg.getId().toString(), ModelLocationUtils.decorateItemModelLocation("handheld"))
                        .texture("layer0", ResourceLocation.fromNamespaceAndPath(reg.getId().getNamespace(), "item/" + reg.getId().getPath()));
        }
    }
}