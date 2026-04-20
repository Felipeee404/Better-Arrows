package com.mfelipeee.betterarrows;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.tags.BlockTags;

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

        boolean softBlock =
                state.is(BlockTags.LOGS) ||
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
                block == Blocks.HONEY_BLOCK ||
                block == Blocks.SLIME_BLOCK ||
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
                block == Blocks.SCULK_SENSOR ||
                block == Blocks.SCULK_SHRIEKER ||
                block == Blocks.SCULK_CATALYST ||

                block == Blocks.CRAFTING_TABLE ||
                block == Blocks.LADDER ||

                block == Blocks.SCAFFOLDING ||
                block == Blocks.TARGET ||

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

        if (softBlock) return;

        boolean hardBlock =
                block == Blocks.OBSIDIAN ||
                block == Blocks.ANVIL ||
                block == Blocks.REINFORCED_DEEPSLATE ||
                block == Blocks.BEDROCK;

        if (hardBlock) {
            if (arrow.level().random.nextFloat() < 0.25f) {
                arrow.discard();
                return;
            }
        }

        arrow.level().playSound(
        null,
        hit.getBlockPos(),
        SoundEvents.ARROW_HIT,
        SoundSource.PLAYERS,
        0.8f,
        1.0f
);

        // 🪨 RICOCHETE
        Vec3 velocity = arrow.getDeltaMovement();
        Vec3 hitPos = hit.getLocation();
        Vec3 blockCenter = Vec3.atCenterOf(hit.getBlockPos());

        Vec3 normal = hitPos.subtract(blockCenter);
        if (normal.lengthSqr() == 0) return;

        normal = normal.normalize();

        double dot = velocity.dot(normal);
        Vec3 reflected = velocity.subtract(normal.scale(2 * dot));

        double maxSpeed = 1.2;
        if (reflected.length() > maxSpeed) {
            reflected = reflected.normalize().scale(maxSpeed);
        }

        double loss = 0.65;
        reflected = reflected.scale(1.0 - loss);

        arrow.setDeltaMovement(reflected);

        arrow.setPos(
                hitPos.x + reflected.x * 0.01,
                hitPos.y + reflected.y * 0.01,
                hitPos.z + reflected.z * 0.01
        );

        arrow.hasImpulse = true;
        arrow.hurtMarked = true;

        event.setCanceled(true);
    }
}