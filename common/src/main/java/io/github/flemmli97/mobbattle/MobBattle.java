package io.github.flemmli97.mobbattle;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MobBattle {

    public static final String MODID = "mobbattle";
    public static boolean tenshiLib;
    public static final Logger LOGGER = LogManager.getLogger(MobBattle.MODID);

    public static TagKey<EntityType<?>> IGNORED = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(MobBattle.MODID, "ignored_mobs"));
    public static TagKey<EntityType<?>> HURT_IGNORED = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(MobBattle.MODID, "hurt_ignored_mobs"));

    @SuppressWarnings("unchecked")
    public static <T> T getPlatformInstance(Class<T> abstractClss, String fabricImpl, String forgeImpl) {
        Class<?> clss = null;
        try {
            clss = Class.forName(forgeImpl);
        } catch (ClassNotFoundException e) {
            try {
                clss = Class.forName(fabricImpl);
            } catch (ClassNotFoundException ex) {
                MobBattle.LOGGER.fatal("No Implementation of {} found with given paths {} and {}", abstractClss, forgeImpl, fabricImpl);
            }
        }
        if (clss != null && abstractClss.isAssignableFrom(clss)) {
            try {
                Constructor<T> constructor = (Constructor<T>) clss.getDeclaredConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                MobBattle.LOGGER.fatal("Implementation of {} needs to provide an no arg constructor", clss);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                MobBattle.LOGGER.error(e);
            }
        }
        throw new IllegalStateException("Couldn't create an instance of " + abstractClss);
    }
}
