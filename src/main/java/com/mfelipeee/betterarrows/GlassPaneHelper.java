package com.mfelipeee.betterarrows;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class GlassPaneHelper {

    public static boolean isGlassPane(Block block) {

        return block == Blocks.GLASS_PANE ||
                block == Blocks.WHITE_STAINED_GLASS_PANE ||
                block == Blocks.ORANGE_STAINED_GLASS_PANE ||
                block == Blocks.MAGENTA_STAINED_GLASS_PANE ||
                block == Blocks.LIGHT_BLUE_STAINED_GLASS_PANE ||
                block == Blocks.YELLOW_STAINED_GLASS_PANE ||
                block == Blocks.LIME_STAINED_GLASS_PANE ||
                block == Blocks.PINK_STAINED_GLASS_PANE ||
                block == Blocks.GRAY_STAINED_GLASS_PANE ||
                block == Blocks.LIGHT_GRAY_STAINED_GLASS_PANE ||
                block == Blocks.CYAN_STAINED_GLASS_PANE ||
                block == Blocks.PURPLE_STAINED_GLASS_PANE ||
                block == Blocks.BLUE_STAINED_GLASS_PANE ||
                block == Blocks.BROWN_STAINED_GLASS_PANE ||
                block == Blocks.GREEN_STAINED_GLASS_PANE ||
                block == Blocks.RED_STAINED_GLASS_PANE ||
                block == Blocks.BLACK_STAINED_GLASS_PANE;
    }
}