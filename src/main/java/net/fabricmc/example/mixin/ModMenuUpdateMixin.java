package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terraformersmc.modmenu.gui.ModsScreen;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.fabricmc.example.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class ModMenuUpdateMixin{
	@Shadow
	public Screen currentScreen;
	@Inject(method = "setScreen", at = @At("HEAD"))
	void setScreen(Screen screen, CallbackInfo info){
		if(currentScreen instanceof ClothConfigScreen) ModConfig.get().applyMenuChanges();
		if(screen instanceof ModsScreen) ModConfig.get().applyGameChanges();
	}
}
