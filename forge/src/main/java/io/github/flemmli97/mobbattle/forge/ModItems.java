package io.github.flemmli97.mobbattle.forge;

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
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MobBattle.MODID);

    public static final RegistryObject<Item> mobStick = ITEMS.register("mob_stick", () -> new MobStick(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobKill = ITEMS.register("mob_kill", () -> new MobKill(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobHeal = ITEMS.register("mob_heal", () -> new MobHeal(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobEffect = ITEMS.register("mob_effect", () -> new MobEffect(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobGroup = ITEMS.register("mob_group", () -> new MobGroup(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobArmor = ITEMS.register("mob_armor", () -> new MobArmor(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobMount = ITEMS.register("mob_mount", () -> new MobMount(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobArmy = ITEMS.register("mob_army", () -> new MobArmy(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobEquip = ITEMS.register("mob_equip", () -> new MobEquip(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> mobEffectGiver = ITEMS.register("mob_effect_give", () -> new MobEffectGive(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> spawner = ITEMS.register("egg_ex", () -> new ItemExtendedSpawnEgg(new Item.Properties()));
}
