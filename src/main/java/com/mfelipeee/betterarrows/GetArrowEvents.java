package com.mfelipeee.betterarrows;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = BetterArrows.MODID)
public class GetArrowEvents {

    private static final float RECOVERY_CHANCE_PER_ARROW = 0.60f;

    @SubscribeEvent
    public static void onEntityHit(LivingIncomingDamageEvent event) {
        var source = event.getSource();

        if (source.getDirectEntity() instanceof AbstractArrow arrow &&
                source.getEntity() == arrow.getOwner() &&
                event.getEntity() instanceof LivingEntity entity) {
            int arrows = entity.getPersistentData().getInt("arrows_stuck");
            entity.getPersistentData().putInt("arrows_stuck", arrows + 1);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        var entity = event.getEntity();
        int arrows = entity.getPersistentData().getInt("arrows_stuck");

        if (arrows <= 0) {
            return;
        }

        int recovered = 0;
        for (int i = 0; i < arrows; i++) {
            if (entity.level().random.nextFloat() <= RECOVERY_CHANCE_PER_ARROW) {
                recovered++;
            }
        }

        if (recovered > 0) {
            ItemEntity arrowDrop = new ItemEntity(
                    entity.level(),
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    new ItemStack(Items.ARROW, recovered)
            );
            entity.level().addFreshEntity(arrowDrop);
        }

        entity.getPersistentData().putInt("arrows_stuck", 0);
        BetterArrows.LOGGER.debug("Recovered {} arrows from {} stuck arrows.", recovered, arrows);
    }
}
