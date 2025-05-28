package io.github.flemmli97.mobbattle.fabric.registry;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModItems {

    private static final List<Item> items = new ArrayList<>();

    public static Item mobStick;
    public static Item mobKill;
    public static Item mobHeal;
    public static Item mobEffect;
    public static Item mobGroup;
    public static Item mobArmor;
    public static Item mobMount;
    public static Item mobArmy;
    public static Item mobEquip;
    public static Item mobEffectGiver;
    public static Item spawner;

    public static void registerItems() {
        mobStick = registerItem("mob_stick", id -> new MobStick(mainProp(id)));
        mobKill = registerItem("mob_kill", id -> new MobKill(mainProp(id)));
        mobHeal = registerItem("mob_heal", id -> new MobHeal(mainProp(id)));
        mobEffect = registerItem("mob_effect", id -> new MobEffect(mainProp(id)));
        mobGroup = registerItem("mob_group", id -> new MobGroup(mainProp(id)));
        mobArmor = registerItem("mob_armor", id -> new MobArmor(mainProp(id)));
        mobMount = registerItem("mob_mount", id -> new MobMount(mainProp(id)));
        mobArmy = registerItem("mob_army", id -> new MobArmy(mainProp(id)));
        mobEquip = registerItem("mob_equip", id -> new MobEquip(mainProp(id)));
        mobEffectGiver = registerItem("mob_effect_give", id -> new MobEffectGive(mainProp(id)));
        spawner = registerItem("egg_ex", id -> new ItemExtendedSpawnEgg(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
        DispenserBlock.registerBehavior(ModItems.spawner, (source, stack) -> {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            double x = source.center().x() + direction.getStepX();
            double y = source.pos().getY() + direction.getStepY() + 0.2;
            double z = source.center().z() + direction.getStepZ();
            BlockPos blockpos = BlockPos.containing(x, y, z);
            boolean spawned = ItemExtendedSpawnEgg.spawnEntity(source.level(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D, direction);
            if (spawned) {
                stack.shrink(1);
            }
            return stack;
        });
    }

    public static List<Item> modItems() {
        return ImmutableList.copyOf(items);
    }

    private static Item.Properties mainProp(ResourceLocation id) {
        return new Item.Properties().stacksTo(1)
                .attributes(new ItemAttributeModifiers(List.of(
                        new ItemAttributeModifiers.Entry(Attributes.ENTITY_INTERACTION_RANGE,
                                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MobBattle.MODID, "stick_mod"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                ))).setId(ResourceKey.create(Registries.ITEM, id));
    }

    private static Item registerItem(String name, Function<ResourceLocation, Item> item) {
        ResourceLocation id = MobBattle.of(name);
        Item registered = Registry.register(BuiltInRegistries.ITEM, id, item.apply(id));
        items.add(registered);
        return registered;
    }
}
