package net.fabricmc.example.event;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.example.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

public class KeyInputHandler {
//	public static boolean glowing = true;
//	public static boolean tracing = true;
	public static boolean display = true;
//	public static int gammamod = 5;
	public static KeyBinding glowtoggle;
	public static KeyBinding gammakey;
	public static KeyBinding fovkey;
	public static KeyBinding statskey;
	public static KeyBinding linekey;
	
	public static void registerKeyInputs(){
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(linekey.wasPressed()){
				ModConfig.get().lines.active = !ModConfig.get().lines.active;
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r Migrator tracing: " + ModConfig.get().lines.active));
			}
			if(glowtoggle.wasPressed()){
				ModConfig.get().glow.active = !ModConfig.get().glow.active;
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r Migrator glowing: " + ModConfig.get().glow.active));
			}
			if(gammakey.wasPressed()){
				int current = ModConfig.get().gamma.current;
				if(ModConfig.get().getToggle()) ModConfig.get().gamma.setToggled();
				else ModConfig.get().gamma.setShifted(!Screen.hasShiftDown());
				if(ModConfig.get().gamma.current != 0 && current != 0)
					Option.GAMMA.set(client.options, Math.abs(client.options.gamma / current * ModConfig.get().gamma.current) * (ModConfig.get().gamma.current < 0 ? -1 : 1));
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r Gamma Multiplier: " + ModConfig.get().gamma.current));
			}
			if(fovkey.wasPressed()){
//				if(!Screen.hasAltDown()) // freeze with fov < 1
//					if(client.options.fov > 10) client.options.fov += Screen.hasShiftDown() ? -10 : 10;
//					else client.options.fov *= Screen.hasShiftDown() ? (client.options.fov >= 10) ? 0.1 : 1 : 10;
//				else client.options.fov = client.options.fov > 70 ? 30 : 110;
				if(ModConfig.get().getToggle()) ModConfig.get().fov.setToggled();
				else ModConfig.get().fov.setShifted(!Screen.hasShiftDown());
				client.options.fov = ModConfig.get().fov.current;
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r FOV: " + (float)client.options.fov));
			}
			if(statskey.wasPressed()){
				// movement tests
//				client.inGameHud.getChatHud().addMessage(Text.of(String.valueOf(ModConfig.get().glow.color.toargb())));
//				client.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(1, 1, false, false));
//				client.player.setVelocityClient(0.1, 0, 0);
//				new KeyBinding();
//				client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(client.player.getX()+0.1, client.player.getY(), client.player.getZ(), client.player.getYaw(), client.player.getPitch(), client.player.isOnGround()));
				if(Screen.hasAltDown()){
					display = !display;
					client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r Showing migrator stats: " + display));
				}
				else {
					ArrayList<Text> entries = MigratorStatsScreen.getMigratorStats(client);
					client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r§c Migrators[" + entries.size() + "]"));
					for(Text i : entries) client.inGameHud.getChatHud().addMessage(i);
				}
			}
		});
	}
	
	public static void register(){
		glowtoggle = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.toggleglow", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.amogus"));
		gammakey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.gammaboost", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.amogus"));
		fovkey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.fovchange", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.amogus"));
		statskey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.stats", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.categories.amogus"));
		linekey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.line", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.categories.amogus"));
		registerKeyInputs();
	}
}
