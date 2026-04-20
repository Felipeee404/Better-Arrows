package com.mfelipeee.betterarrows;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;

public class GlassCrackData {

    private static final Map<BlockPos, Integer> cracks = new HashMap<>();

    public static int addCrack(BlockPos pos) {
        int c = cracks.getOrDefault(pos, 0) + 1;
        cracks.put(pos, c);
        return c;
    }

    public static int getCracks(BlockPos pos) {
        return cracks.getOrDefault(pos, 0);
    }

    public static void clear(BlockPos pos) {
        cracks.remove(pos);
    }
}