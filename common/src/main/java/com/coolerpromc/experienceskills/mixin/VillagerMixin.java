package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Inject(
        method = "updateSpecialPrices",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/Villager;getTradingPlayer()Lnet/minecraft/world/entity/player/Player;")
    )
    private void applyTradeDiscount(Player player, CallbackInfo ci){
        double discount = player.getAttributeValue(ModAttributes.TRADE_DISCOUNT.holder());
        if (discount <= 0) return;

        for (MerchantOffer offer : ((Villager) (Object) this).getOffers()){
            int reduction = (int) Math.floor(discount * offer.getBaseCostA().getCount());
            if (reduction > 0){
                offer.addToSpecialPriceDiff(-reduction);
            }
        }
    }
}
