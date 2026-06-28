package com.mfelipeee.betterarrows;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_MATERIAL_SOUNDS = BUILDER
            .comment("Plays different impact sounds depending on the block material.")
            .define("enableMaterialSounds", true);

    public static final ModConfigSpec.BooleanValue ENABLE_WATER_SLOWDOWN = BUILDER
            .comment("Gradually slows arrows after they touch water.")
            .define("enableWaterSlowdown", true);

    public static final ModConfigSpec.IntValue WATER_SLOWDOWN_DURATION_TICKS = BUILDER
            .comment("How long the water slowdown effect should linger after contact, in ticks.")
            .defineInRange("waterSlowdownDurationTicks", 60, 0, 240);

    public static final ModConfigSpec.DoubleValue WATER_SLOWDOWN_IN_WATER = BUILDER
            .comment("Arrow speed multiplier applied each tick while the arrow is in water.")
            .defineInRange("waterSlowdownInWater", 0.92D, 0.5D, 1.0D);

    public static final ModConfigSpec.DoubleValue WATER_SLOWDOWN_AFTER_WATER = BUILDER
            .comment("Arrow speed multiplier applied each tick for a short time after leaving water.")
            .defineInRange("waterSlowdownAfterWater", 0.97D, 0.5D, 1.0D);

    static final ModConfigSpec SPEC = BUILDER.build();
}
