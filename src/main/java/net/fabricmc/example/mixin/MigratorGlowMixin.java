package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.example.GlowHelper;
import net.fabricmc.example.event.KeyInputHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class MigratorGlowMixin{
	
	@Inject(at = @At("RETURN"), method = "isGlowing()Z", cancellable = true)
	void isGlowing(CallbackInfoReturnable<Boolean> info){
		if(!KeyInputHandler.glowing || info.getReturnValueZ() || !((Object)this instanceof AbstractClientPlayerEntity)) return;
		info.setReturnValue(GlowHelper.isMigrator((AbstractClientPlayerEntity)(Object)this));
	}
}
