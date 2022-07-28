package io.github.flemmli97.mobbattle.fabric;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.MobBattleTab;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;

public class ModItems {

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
        mobStick = registerItem("mob_stick", new MobStick(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobKill = registerItem("mob_kill", new MobKill(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobHeal = registerItem("mob_heal", new MobHeal(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobEffect = registerItem("mob_effect", new MobEffect(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobGroup = registerItem("mob_group", new MobGroup(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobArmor = registerItem("mob_armor", new MobArmor(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobMount = registerItem("mob_mount", new MobMount(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobArmy = registerItem("mob_army", new MobArmy(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobEquip = registerItem("mob_equip", new MobEquip(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        mobEffectGiver = registerItem("mob_effect_give", new MobEffectGive(new Item.Properties().stacksTo(1).tab(MobBattleTab.customTab)));
        spawner = registerItem("egg_ex", new ItemExtendedSpawnEgg(new Item.Properties().tab(MobBattleTab.customTab)));
        DispenserBlock.registerBehavior(ModItems.spawner, (source, stack) -> {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            double x = source.x() + direction.getStepX();
            double y = source.getPos().getY() + direction.getStepY() + 0.2;
            double z = source.z() + direction.getStepZ();
            BlockPos blockpos = new BlockPos(x, y, z);
            Entity entity = ItemExtendedSpawnEgg.spawnEntity(source.getLevel(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D);
            if (entity != null) {
                stack.shrink(1);
                if (stack.hasCustomHoverName() && entity instanceof Mob) {
                    Utils.updateEntity(stack.getHoverName().getString(), (Mob) entity);
                }
            }
            return stack;
        });
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(MobBattle.MODID, name), item);
    }
}
