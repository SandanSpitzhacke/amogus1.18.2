package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class F3AddTogglesMixin {
	/*// unused in 1.18 but important in 1.19
	
	@Shadow @Final
	private MinecraftClient client;
	
	@Inject(method = "processF3(I)Z", at = @At("HEAD"))
	void processF3(int key, CallbackInfoReturnable<Boolean> info){
		if(key == 86) { // V toggles FOV
			if(client.options.fov <= 10) client.options.fov *= Screen.hasShiftDown() ? 0.1 : 10;
			else client.options.fov += Screen.hasShiftDown() ? -10 : 10;
			client.inGameHud.getChatHud().addMessage(Text.of("§4[\u0d9e]:§r FOV: " + client.options.fov));
		}
		if(key == 69) { // E toggles gamma
			KeyInputHandler.gammamod += Screen.hasShiftDown() ? -1 : 1;
			client.inGameHud.getChatHud().addMessage(Text.of("§4[\u0d9e]:§r Gamma modifier: " + KeyInputHandler.gammamod));
		}
		if(key == 70) { // F toggles render distance
            Option.RENDER_DISTANCE.set(this.client.options, MathHelper.clamp((double)(this.client.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1)), Option.RENDER_DISTANCE.getMin(), Option.RENDER_DISTANCE.getMax()));
            this.debugLog("debug.cycle_renderdistance.message", this.client.options.viewDistance);
            return true;
        }
	}
	*/
}
