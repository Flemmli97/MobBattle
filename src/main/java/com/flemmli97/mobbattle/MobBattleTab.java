package com.flemmli97.mobbattle;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MobBattleTab {

    public static ItemGroup customTab = new ItemGroup("mobbattle") {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.mobStick);
        }
    };
}
