package com.mfelipeee.betterarrows;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber(modid = BetterArrows.MODID)
public class ArrowEvents {

    @SubscribeEvent
    public static void onArrowHit(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
        if (!(event.getRayTraceResult() instanceof BlockHitResult hit)) return;
        if (arrow.isRemoved()) return;

        BlockState state = arrow.level().getBlockState(hit.getBlockPos());
        Block block = state.getBlock();
        BounceProfile profile = getBounceProfile(state, block);

        if (profile == null) {
            return;
        }

        Vec3 velocity = arrow.getDeltaMovement();
        Direction direction = hit.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal()).normalize();
        double incomingSpeed = velocity.dot(normal);

        if (incomingSpeed >= 0.0D) {
            return;
        }

        if (arrow.level().random.nextFloat() > profile.ricochetChance()) {
            return;
        }

        Vec3 reflected = velocity.subtract(normal.scale(2.0D * incomingSpeed));
        reflected = reflected.scale(profile.energyRetention());

        double maxSpeed = profile.maxSpeed();
        if (reflected.lengthSqr() > maxSpeed * maxSpeed) {
            reflected = reflected.normalize().scale(maxSpeed);
        }

        if (reflected.lengthSqr() < 0.0025D) {
            return;
        }

        arrow.level().playSound(
                null,
                hit.getBlockPos(),
                SoundEvents.ARROW_HIT,
                SoundSource.PLAYERS,
                0.8f,
                1.0f
        );

        arrow.setDeltaMovement(reflected);

        Vec3 hitPos = hit.getLocation();
        arrow.setPos(
                hitPos.x + normal.x * 0.06D,
                hitPos.y + normal.y * 0.06D,
                hitPos.z + normal.z * 0.06D
        );

        arrow.hasImpulse = true;
        arrow.hurtMarked = true;
        event.setCanceled(true);
    }

    private static BounceProfile getBounceProfile(BlockState state, Block block) {
        if (isAbsorbingBlock(state, block)) {
            return null;
        }

        if (block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK) {
            return new BounceProfile(1.0D, 0.92D, 1.45D);
        }

        if (isFragileBlock(block)) {
            return new BounceProfile(0.85D, 0.30D, 0.90D);
        }

        if (isHardBlock(block)) {
            return new BounceProfile(1.0D, 0.78D, 1.20D);
        }

        if (block == Blocks.TARGET ||
                block == Blocks.SCULK_SENSOR ||
                block == Blocks.SCULK_SHRIEKER ||
                block == Blocks.SCULK_CATALYST) {
            return new BounceProfile(1.0D, 0.66D, 1.05D);
        }

        return new BounceProfile(0.95D, 0.52D, 1.00D);
    }

    private static boolean isAbsorbingBlock(BlockState state, Block block) {
        return state.is(BlockTags.LOGS) ||
                state.is(BlockTags.PLANKS) ||
                state.is(BlockTags.WOODEN_DOORS) ||
                state.is(BlockTags.WOODEN_TRAPDOORS) ||
                state.is(BlockTags.WOODEN_STAIRS) ||
                state.is(BlockTags.WOODEN_SLABS) ||
                state.is(BlockTags.SIGNS) ||
                state.is(BlockTags.FENCE_GATES) ||
                state.is(BlockTags.WOODEN_FENCES) ||
                state.is(BlockTags.BAMBOO_BLOCKS) ||
                block == Blocks.BAMBOO_MOSAIC ||
                block == Blocks.BAMBOO_MOSAIC_SLAB ||
                block == Blocks.BAMBOO_MOSAIC_STAIRS ||
                block == Blocks.BAMBOO_FENCE_GATE ||
                block == Blocks.BOOKSHELF ||
                block == Blocks.CHISELED_BOOKSHELF ||
                block == Blocks.JUKEBOX ||
                block == Blocks.NOTE_BLOCK ||
                block == Blocks.COMPOSTER ||
                state.is(BlockTags.LEAVES) ||
                state.is(BlockTags.SAPLINGS) ||
                state.is(BlockTags.FLOWERS) ||
                state.is(BlockTags.CROPS) ||
                state.is(BlockTags.DIRT) ||
                state.is(BlockTags.SAND) ||
                block == Blocks.GRAVEL ||
                block == Blocks.CLAY ||
                block == Blocks.MUD ||
                block == Blocks.DIRT_PATH ||
                block == Blocks.SANDSTONE ||
                block == Blocks.RED_SANDSTONE ||
                state.is(BlockTags.WOOL) ||
                state.is(BlockTags.BEDS) ||
                block == Blocks.MOSS_CARPET ||
                block == Blocks.SPONGE ||
                block == Blocks.HAY_BLOCK ||
                block == Blocks.SNIFFER_EGG ||
                block == Blocks.TURTLE_EGG ||
                block == Blocks.PUMPKIN ||
                block == Blocks.MELON ||
                block == Blocks.VINE ||
                block == Blocks.CACTUS ||
                block == Blocks.BEE_NEST ||
                block == Blocks.BEEHIVE ||
                block == Blocks.SCULK ||
                block == Blocks.CRAFTING_TABLE ||
                block == Blocks.LADDER ||
                block == Blocks.SCAFFOLDING ||
                block == Blocks.CHEST ||
                block == Blocks.BARREL ||
                block == Blocks.SHULKER_BOX ||
                block == Blocks.FLETCHING_TABLE ||
                block == Blocks.CARTOGRAPHY_TABLE ||
                block == Blocks.MANGROVE_ROOTS ||
                block == Blocks.OCHRE_FROGLIGHT ||
                block == Blocks.VERDANT_FROGLIGHT ||
                block == Blocks.PEARLESCENT_FROGLIGHT ||
                block == Blocks.CAMPFIRE ||
                block == Blocks.SOUL_CAMPFIRE ||
                state.is(BlockTags.CONCRETE_POWDER);
    }

    private static boolean isFragileBlock(Block block) {
        return block == Blocks.GLASS ||
                block == Blocks.TINTED_GLASS ||
                GlassPaneHelper.isGlassPane(block) ||
                block == Blocks.ICE ||
                block == Blocks.PACKED_ICE ||
                block == Blocks.BLUE_ICE;
    }

    private static boolean isHardBlock(Block block) {
        return block == Blocks.OBSIDIAN ||
                block == Blocks.ANVIL ||
                block == Blocks.REINFORCED_DEEPSLATE ||
                block == Blocks.BEDROCK ||
                block == Blocks.IRON_BLOCK ||
                block == Blocks.IRON_BARS ||
                block == Blocks.DEEPSLATE ||
                block == Blocks.COPPER_BLOCK ||
                block == Blocks.EXPOSED_COPPER ||
                block == Blocks.WEATHERED_COPPER ||
                block == Blocks.OXIDIZED_COPPER;
    }

    private record BounceProfile(double ricochetChance, double energyRetention, double maxSpeed) {
    }
}
