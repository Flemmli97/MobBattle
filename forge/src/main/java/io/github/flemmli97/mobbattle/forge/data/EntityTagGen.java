package io.github.flemmli97.mobbattle.forge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EntityTagGen extends TagsProvider<EntityType<?>> {

    @SuppressWarnings("deprecation")
    public EntityTagGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Registry.ENTITY_TYPE, MobBattle.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(MobBattle.HURT_IGNORED)
                .add(EntityType.VEX);
    }

    @Override
    public String getName() {
        return "Entity Tags";
    }
}
