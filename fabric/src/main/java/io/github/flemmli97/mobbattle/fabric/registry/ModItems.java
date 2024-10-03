package io.github.flemmli97.mobbattle.fabric.registry;

import com.google.common.collect.ImmutableList;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.handler.Utils;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.List;

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
        mobStick = registerItem("mob_stick", new MobStick(mainProp()));
        mobKill = registerItem("mob_kill", new MobKill(mainProp()));
        mobHeal = registerItem("mob_heal", new MobHeal(mainProp()));
        mobEffect = registerItem("mob_effect", new MobEffect(mainProp()));
        mobGroup = registerItem("mob_group", new MobGroup(mainProp()));
        mobArmor = registerItem("mob_armor", new MobArmor(mainProp()));
        mobMount = registerItem("mob_mount", new MobMount(mainProp()));
        mobArmy = registerItem("mob_army", new MobArmy(mainProp()));
        mobEquip = registerItem("mob_equip", new MobEquip(mainProp()));
        mobEffectGiver = registerItem("mob_effect_give", new MobEffectGive(mainProp()));
        spawner = registerItem("egg_ex", new ItemExtendedSpawnEgg(new Item.Properties()));
        DispenserBlock.registerBehavior(ModItems.spawner, (source, stack) -> {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            double x = source.center().x() + direction.getStepX();
            double y = source.pos().getY() + direction.getStepY() + 0.2;
            double z = source.center().z() + direction.getStepZ();
            BlockPos blockpos = BlockPos.containing(x, y, z);
            Entity entity = ItemExtendedSpawnEgg.spawnEntity(source.level(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D);
            if (entity != null) {
                stack.shrink(1);
                if (stack.has(DataComponents.CUSTOM_NAME) && entity instanceof Mob) {
                    Utils.updateEntity(stack.getHoverName().getString(), (Mob) entity);
                }
            }
            return stack;
        });
    }

    public static List<Item> modItems() {
        return ImmutableList.copyOf(items);
    }

    private static Item.Properties mainProp() {
        return new Item.Properties().stacksTo(1)
                .attributes(new ItemAttributeModifiers(List.of(
                        new ItemAttributeModifiers.Entry(Attributes.ENTITY_INTERACTION_RANGE,
                                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MobBattle.MODID, "stick_mod"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                ), false));
    }

    private static Item registerItem(String name, Item item) {
        Item registered = Registry.register(BuiltInRegistries.ITEM, MobBattle.of(name), item);
        items.add(registered);
        return registered;
    }
}
