package com.mfelipeee.betterarrows;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = BetterArrows.MODID)
public class GetArrowEvents {

    // 🏹 Quando flecha acerta entidade
    @SubscribeEvent
    public static void onEntityHit(LivingIncomingDamageEvent event) {

        var source = event.getSource();

        if (source.getDirectEntity() instanceof AbstractArrow arrow &&
    event.getSource().getEntity() == arrow.getOwner()) {

            if (event.getEntity() instanceof LivingEntity entity) {

                int arrows = entity.getPersistentData().getInt("arrows_stuck");

                entity.getPersistentData().putInt("arrows_stuck", arrows + 1);

                System.out.println("Flechas presas: " + (arrows + 1));
            }
        }
    }

    // 💀 Quando entidade morre
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {

        var entity = event.getEntity();

        int arrows = entity.getPersistentData().getInt("arrows_stuck");

        if (arrows > 0) {

            // ⚖️ Balanceamento (60% recupera)
            int recovered = (int)(arrows * 0.6);

            for (int i = 0; i < recovered; i++) {

                ItemEntity arrowDrop = new ItemEntity(
                        entity.level(),
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        new ItemStack(Items.ARROW)
                );

                entity.level().addFreshEntity(arrowDrop);
            }

            // limpa contador
            entity.getPersistentData().putInt("arrows_stuck", 0);

            System.out.println("Dropou " + recovered + " flechas!");
        }
    }
}