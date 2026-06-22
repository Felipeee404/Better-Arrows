package com.mfelipeee.betterarrows;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
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

        BlockPos pos = hit.getBlockPos();
        BlockState state = arrow.level().getBlockState(pos);
        Block block = state.getBlock();
        ImpactProfile profile = getImpactProfile(arrow, pos, state, block, hit.getDirection());

        if (profile == null) {
            return;
        }

        if (shouldBreak(arrow, profile)) {
            spawnBrokenArrow(arrow, hitPos(arrow, hit));
            arrow.level().playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.8f, 1.0f);
            arrow.discard();
            event.setCanceled(true);
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

        arrow.level().playSound(null, pos, SoundEvents.ARROW_HIT, SoundSource.PLAYERS, 0.8f, 1.0f);

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

    private static ImpactProfile getImpactProfile(AbstractArrow arrow, BlockPos pos, BlockState state, Block block, Direction impactDirection) {
        if (block == Blocks.HONEY_BLOCK) {
            return null;
        }

        if (isWoodLikeBlock(state, block)) {
            return null;
        }

        if (block == Blocks.SLIME_BLOCK) {
            return new ImpactProfile(0.0D, 1.0D, 1.14D, 1.75D);
        }

        if (isStoneLikeBlock(arrow.level(), pos, state, block, impactDirection)) {
            return new ImpactProfile(getBreakChance(arrow.level(), pos, state, block), 1.0D, 0.28D, 0.90D);
        }

        return null;
    }

    private static boolean shouldBreak(AbstractArrow arrow, ImpactProfile profile) {
        if (profile.breakChance() <= 0.0D) {
            return false;
        }

        return arrow.level().random.nextFloat() < profile.breakChance();
    }

    private static void spawnBrokenArrow(AbstractArrow arrow, Vec3 location) {
        boolean bent = arrow.level().random.nextBoolean();
        ItemStack stack = new ItemStack(
                bent ? BetterArrows.BROKEN_ARROW_BENT.get() : BetterArrows.BROKEN_ARROW_TIP.get()
        );

        ItemEntity itemEntity = new ItemEntity(arrow.level(), location.x, location.y, location.z, stack);
        itemEntity.setDeltaMovement(arrow.getDeltaMovement().scale(0.15D));
        arrow.level().addFreshEntity(itemEntity);
    }

    private static Vec3 hitPos(AbstractArrow arrow, BlockHitResult hit) {
        Vec3 base = hit.getLocation();
        Direction direction = hit.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal()).normalize();
        return base.add(normal.scale(0.06D));
    }

    private static double getBreakChance(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, Block block) {
        float hardness = state.getDestroySpeed(level, pos);

        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            return 1.0D;
        }

        if (block == Blocks.REINFORCED_DEEPSLATE || block == Blocks.ANVIL) {
            return 0.75D;
        }

        if (hardness >= 10.0F) {
            return 0.55D;
        }

        if (hardness >= 5.0F) {
            return 0.30D;
        }

        return 0.12D;
    }

    private static boolean isWoodLikeBlock(BlockState state, Block block) {
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
                block == Blocks.CAMPFIRE ||
                block == Blocks.SOUL_CAMPFIRE ||
                state.is(BlockTags.CONCRETE_POWDER);
    }

    private static boolean isStoneLikeBlock(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, Block block, Direction impactDirection) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) ||
                state.is(BlockTags.BASE_STONE_NETHER) ||
                (state.getDestroySpeed(level, pos) >= 1.5F &&
                        state.isFaceSturdy(level, pos, impactDirection.getOpposite())) ||
                block == Blocks.OBSIDIAN ||
                block == Blocks.ANVIL ||
                block == Blocks.REINFORCED_DEEPSLATE ||
                block == Blocks.BEDROCK ||
                block == Blocks.DEEPSLATE ||
                block == Blocks.COBBLED_DEEPSLATE ||
                block == Blocks.POLISHED_DEEPSLATE ||
                block == Blocks.DEEPSLATE_BRICKS ||
                block == Blocks.DEEPSLATE_TILES ||
                block == Blocks.CRACKED_DEEPSLATE_BRICKS ||
                block == Blocks.CRACKED_DEEPSLATE_TILES ||
                block == Blocks.STONE ||
                block == Blocks.COBBLESTONE ||
                block == Blocks.MOSSY_COBBLESTONE ||
                block == Blocks.STONE_BRICKS ||
                block == Blocks.MOSSY_STONE_BRICKS ||
                block == Blocks.CRACKED_STONE_BRICKS ||
                block == Blocks.CHISELED_STONE_BRICKS ||
                block == Blocks.GRANITE ||
                block == Blocks.POLISHED_GRANITE ||
                block == Blocks.DIORITE ||
                block == Blocks.POLISHED_DIORITE ||
                block == Blocks.ANDESITE ||
                block == Blocks.POLISHED_ANDESITE ||
                block == Blocks.TUFF ||
                block == Blocks.CALCITE ||
                block == Blocks.SMOOTH_BASALT ||
                block == Blocks.IRON_BLOCK ||
                block == Blocks.IRON_BARS ||
                block == Blocks.COPPER_BLOCK ||
                block == Blocks.EXPOSED_COPPER ||
                block == Blocks.WEATHERED_COPPER ||
                block == Blocks.OXIDIZED_COPPER;
    }

    private record ImpactProfile(double breakChance, double ricochetChance, double energyRetention, double maxSpeed) {
    }
}
