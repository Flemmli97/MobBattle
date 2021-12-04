package io.github.flemmli97.mobbattle;

import net.minecraft.world.entity.EquipmentSlot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobBattle {

    public static final String MODID = "mobbattle";
    public static boolean tenshiLib;
    public static final Logger logger = LogManager.getLogger(MobBattle.MODID);

    public static final EquipmentSlot[] slot = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
}
