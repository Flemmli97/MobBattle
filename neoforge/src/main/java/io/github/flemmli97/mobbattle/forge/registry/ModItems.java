package io.github.flemmli97.mobbattle.forge.registry;

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
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MobBattle.MODID);

    public static final DeferredHolder<Item, MobStick> MOB_STICK = ITEMS.register("mob_stick", () -> new MobStick(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobKill> MOB_KILL = ITEMS.register("mob_kill", () -> new MobKill(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobHeal> MOB_HEAL = ITEMS.register("mob_heal", () -> new MobHeal(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobEffect> MOB_EFFECT = ITEMS.register("mob_effect", () -> new MobEffect(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobGroup> MOB_GROUP = ITEMS.register("mob_group", () -> new MobGroup(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobArmor> MOB_ARMOR = ITEMS.register("mob_armor", () -> new MobArmor(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobMount> MOB_MOUNT = ITEMS.register("mob_mount", () -> new MobMount(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobArmy> MOB_ARMY = ITEMS.register("mob_army", () -> new MobArmy(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobEquip> MOB_EQUIP = ITEMS.register("mob_equip", () -> new MobEquip(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, MobEffectGive> MOB_EFFECT_GIVE = ITEMS.register("mob_effect_give", () -> new MobEffectGive(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ItemExtendedSpawnEgg> EXTENDED_EGG = ITEMS.register("egg_ex", () -> new ItemExtendedSpawnEgg(new Item.Properties()));
}
