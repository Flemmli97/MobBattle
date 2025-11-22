package io.github.flemmli97.mobbattle.common.registry;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.mobbattle.common.items.MobArmor;
import io.github.flemmli97.mobbattle.common.items.MobArmy;
import io.github.flemmli97.mobbattle.common.items.MobEffect;
import io.github.flemmli97.mobbattle.common.items.MobEffectGive;
import io.github.flemmli97.mobbattle.common.items.MobEquip;
import io.github.flemmli97.mobbattle.common.items.MobGroup;
import io.github.flemmli97.mobbattle.common.items.MobHeal;
import io.github.flemmli97.mobbattle.common.items.MobKill;
import io.github.flemmli97.mobbattle.common.items.MobMount;
import io.github.flemmli97.mobbattle.common.items.MobStick;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.function.Supplier;

public class MobBattleItems {

    public static final Supplier<MobStick> MOB_STICK = CrossPlatformStuff.INSTANCE.registerItem("mob_stick", () -> new MobStick(mainProp()));
    public static final Supplier<MobKill> MOB_KILL = CrossPlatformStuff.INSTANCE.registerItem("mob_kill", () -> new MobKill(mainProp()));
    public static final Supplier<MobHeal> MOB_HEAL = CrossPlatformStuff.INSTANCE.registerItem("mob_heal", () -> new MobHeal(mainProp()));
    public static final Supplier<MobEffect> MOB_EFFECT = CrossPlatformStuff.INSTANCE.registerItem("mob_effect", () -> new MobEffect(mainProp()));
    public static final Supplier<MobGroup> MOB_GROUP = CrossPlatformStuff.INSTANCE.registerItem("mob_group", () -> new MobGroup(mainProp()));
    public static final Supplier<MobArmor> MOB_ARMOR = CrossPlatformStuff.INSTANCE.registerItem("mob_armor", () -> new MobArmor(mainProp()));
    public static final Supplier<MobMount> MOB_MOUNT = CrossPlatformStuff.INSTANCE.registerItem("mob_mount", () -> new MobMount(mainProp()));
    public static final Supplier<MobArmy> MOB_ARMY = CrossPlatformStuff.INSTANCE.registerItem("mob_army", () -> new MobArmy(mainProp()));
    public static final Supplier<MobEquip> MOB_EQUIP = CrossPlatformStuff.INSTANCE.registerItem("mob_equip", () -> new MobEquip(mainProp()));
    public static final Supplier<MobEffectGive> MOB_EFFECT_GIVE = CrossPlatformStuff.INSTANCE.registerItem("mob_effect_give", () -> new MobEffectGive(mainProp()));
    public static final Supplier<ItemExtendedSpawnEgg> EXTENDED_EGG = CrossPlatformStuff.INSTANCE.registerItem("egg_ex", () -> new ItemExtendedSpawnEgg(new Item.Properties()));

    private static Item.Properties mainProp() {
        return new Item.Properties().stacksTo(1)
                .attributes(new ItemAttributeModifiers(List.of(
                        new ItemAttributeModifiers.Entry(Attributes.ENTITY_INTERACTION_RANGE,
                                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MobBattle.MODID, "stick_mod"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                ), false));
    }

    public static void init() {
    }
}
