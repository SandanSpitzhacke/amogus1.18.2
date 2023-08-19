package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.example.ModConfig;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.Text;

@Mixin(DoubleOption.class)
public abstract class GammaUnblockMixin<T>{
	
	@Shadow
	protected double max;

	@Inject(at = @At("HEAD"), method = "getValue(D)D", cancellable = true)
	public void getValue(double ratio, CallbackInfoReturnable<Double> info){
//		if((Object)this == (Object)Option.GAMMA) System.out.println("mixed into gamma");
		if((Object)this == (Object)Option.GAMMA) this.max = ModConfig.get().gamma.current;
//		info.setReturnValue(this.invokeAdjust(MathHelper.lerp(MathHelper.clamp(ratio, 0.0, 1.0), this.min, this.max)));
//		if(KeyInputHandler.gammamod != 1 && text.getString().equals(I18n.translate("options.gamma")))
//			this.value = (T) Double.valueOf(Math.floor(100 * ((Double)value).doubleValue()) / (100 / KeyInputHandler.gammamod));
//		else this.value = value;
//		info.cancel();
	}
	
	@Inject(at = @At("HEAD"), method = "getDisplayString")
	public void getDisplayString(GameOptions options, CallbackInfoReturnable<Text> info){
		if((Object)this == (Object)Option.GAMMA) this.max = ModConfig.get().gamma.current;
	}
	
	@Inject(at = @At("HEAD"), method = "get")
	public void get(GameOptions options, CallbackInfoReturnable<Double> info){
		if((Object)this == (Object)Option.GAMMA) this.max = ModConfig.get().gamma.current;
	}
}