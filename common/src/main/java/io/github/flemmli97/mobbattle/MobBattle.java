package io.github.flemmli97.mobbattle;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class MobBattle {

    public static final String MODID = "mobbattle";
    public static boolean tenshiLib;
    public static final Logger LOGGER = LogManager.getLogger(MobBattle.MODID);

    public static TagKey<EntityType<?>> IGNORED = TagKey.create(Registries.ENTITY_TYPE, MobBattle.of("ignored_mobs"));
    public static TagKey<EntityType<?>> HURT_IGNORED = TagKey.create(Registries.ENTITY_TYPE, MobBattle.of("hurt_ignored_mobs"));

    public static Supplier<CreativeModeTab> customTab;

    @SuppressWarnings("unchecked")
    public static <T> T getPlatformInstance(Class<T> abstractClss, String fabricImpl, String neoForgeImpl) {
        Class<?> clss = null;
        try {
            clss = Class.forName(neoForgeImpl);
        } catch (ClassNotFoundException e) {
            try {
                clss = Class.forName(fabricImpl);
            } catch (ClassNotFoundException ex) {
                MobBattle.LOGGER.fatal("No Implementation of {} found with given paths {} and {}", abstractClss, neoForgeImpl, fabricImpl);
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

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
