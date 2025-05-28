package io.github.flemmli97.mobbattle.neoforge.registry;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.mobbattle.items.MobArmor;
import io.github.flemmli97.mobbattle.items.MobArmy;
import io.github.flemmli97.mobbattle.items.MobEffect;
import io.github.flemmli97.mobbattle.items.MobEffectGive;
import io.github.flemmli97.mobbattle.items.MobEquip;
import io.github.flemmli97.mobbattle.items.MobGroup;
import io.github.flemmli97.mobbattle.items.MobHeal;
import io.github.flemmli97.mobbattle.items.MobKill;
import io.github.flemmli97.mobbattle.items.MobMount;
import io.github.flemmli97.mobbattle.items.MobStick;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MobBattle.MODID);

    public static final DeferredHolder<Item, MobStick> MOB_STICK = ITEMS.register("mob_stick", id -> new MobStick(mainProp(id)));
    public static final DeferredHolder<Item, MobKill> MOB_KILL = ITEMS.register("mob_kill", id -> new MobKill(mainProp(id)));
    public static final DeferredHolder<Item, MobHeal> MOB_HEAL = ITEMS.register("mob_heal", id -> new MobHeal(mainProp(id)));
    public static final DeferredHolder<Item, MobEffect> MOB_EFFECT = ITEMS.register("mob_effect", id -> new MobEffect(mainProp(id)));
    public static final DeferredHolder<Item, MobGroup> MOB_GROUP = ITEMS.register("mob_group", id -> new MobGroup(mainProp(id)));
    public static final DeferredHolder<Item, MobArmor> MOB_ARMOR = ITEMS.register("mob_armor", id -> new MobArmor(mainProp(id)));
    public static final DeferredHolder<Item, MobMount> MOB_MOUNT = ITEMS.register("mob_mount", id -> new MobMount(mainProp(id)));
    public static final DeferredHolder<Item, MobArmy> MOB_ARMY = ITEMS.register("mob_army", id -> new MobArmy(mainProp(id)));
    public static final DeferredHolder<Item, MobEquip> MOB_EQUIP = ITEMS.register("mob_equip", id -> new MobEquip(mainProp(id)));
    public static final DeferredHolder<Item, MobEffectGive> MOB_EFFECT_GIVE = ITEMS.register("mob_effect_give", id -> new MobEffectGive(mainProp(id)));
    public static final DeferredHolder<Item, ItemExtendedSpawnEgg> EXTENDED_EGG = ITEMS.register("egg_ex", id -> new ItemExtendedSpawnEgg(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

    private static Item.Properties mainProp(ResourceLocation id) {
        return new Item.Properties().stacksTo(1)
                .attributes(new ItemAttributeModifiers(List.of(
                        new ItemAttributeModifiers.Entry(Attributes.ENTITY_INTERACTION_RANGE,
                                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MobBattle.MODID, "stick_mod"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                ))).setId(ResourceKey.create(Registries.ITEM, id));
    }
}
