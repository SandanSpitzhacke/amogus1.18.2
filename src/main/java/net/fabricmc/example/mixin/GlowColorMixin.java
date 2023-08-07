package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.example.GlowHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class GlowColorMixin{
	@Inject(at = @At("RETURN"), method = "getTeamColorValue()I", cancellable = true)
	void getTeamColorValue(CallbackInfoReturnable<Integer> info){
		if((Object)this instanceof AbstractClientPlayerEntity) 
			if(GlowHelper.isMigrator((AbstractClientPlayerEntity)(Object)this))
				info.setReturnValue(0xbf1f00);
	}
	
	@Inject(at = @At("RETURN"), method = "isGlowingLocal()Z", cancellable = true)
	void isGlowingLocal(CallbackInfoReturnable<Boolean> info){
		if(info.getReturnValueZ() || !((Object)this instanceof AbstractClientPlayerEntity)) return;
		info.setReturnValue(GlowHelper.isMigrator((AbstractClientPlayerEntity)(Object)this));
	}
} 