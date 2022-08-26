package io.github.flemmli97.mobbattle;

import io.github.flemmli97.tenshilib.TenshiLib;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MobBattle {

    public static final String MODID = "mobbattle";
    public static boolean tenshiLib;
    public static final Logger logger = LogManager.getLogger(MobBattle.MODID);

    public static final EquipmentSlot[] slot = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static TagKey<EntityType<?>> IGNORED = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(MobBattle.MODID, "ignored_mobs"));

    @SuppressWarnings("unchecked")
    public static <T> T getPlatformInstance(Class<T> abstractClss, String fabricImpl, String forgeImpl) {
        Class<?> clss = null;
        try {
            clss = Class.forName(forgeImpl);
        } catch (ClassNotFoundException e) {
            try {
                clss = Class.forName(fabricImpl);
            } catch (ClassNotFoundException ex) {
                TenshiLib.logger.fatal("No Implementation of " + abstractClss + " found with given paths " + forgeImpl + " and " + fabricImpl);
            }
        }
        if (clss != null && abstractClss.isAssignableFrom(clss)) {
            try {
                Constructor<T> constructor = (Constructor<T>) clss.getDeclaredConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                TenshiLib.logger.fatal("Implementation of " + clss + " needs to provide an no arg constructor");
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("Couldn't create an instance of " + abstractClss);
    }
}
