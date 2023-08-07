package net.fabricmc.example.event;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.example.GlowHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class KeyInputHandler {
	public static KeyBinding glowtoggle;
	public static boolean glowing = true;
	public static boolean tracing = true;
	public static KeyBinding gammakey;
	public static int gammamod = 5;
	public static KeyBinding fovkey;
	public static KeyBinding statskey;
	public static KeyBinding linekey;
	
	public static void registerKeyInputs(){
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(linekey.wasPressed()){
				tracing = !tracing;
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r Migrator tracing: " + tracing));
			}
			if(glowtoggle.wasPressed()){
				glowing = !glowing;
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r Migrator glowing: " + glowing));
			}
			if(gammakey.wasPressed()){
				if(!Screen.hasAltDown()) gammamod += Screen.hasShiftDown() ? -1 : 1;
				else gammamod = gammamod > 3 ? 1 : 5;
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r Gamma Multiplier: " + gammamod));
			}
			if(fovkey.wasPressed()){
				if(!Screen.hasAltDown()) // freeze with fov < 1
					if(client.options.fov > 10) client.options.fov += Screen.hasShiftDown() ? -10 : 10;
					else client.options.fov *= Screen.hasShiftDown() ? (client.options.fov >= 10) ? 0.1 : 1 : 10;
				else client.options.fov = client.options.fov > 70 ? 30 : 110;
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r FOV: " + (float)client.options.fov));
			}
			if(statskey.wasPressed()){
				ArrayList<Pair<AbstractClientPlayerEntity, Double>> players = new ArrayList<>();
				for(AbstractClientPlayerEntity player : client.world.getPlayers()) if(GlowHelper.isMigrator(player))
					players.add(new Pair<>(player, player.getPos().distanceTo(client.player.getPos())));
				client.inGameHud.getChatHud().addMessage(Text.of("§4§l[\u0d9e]:§r§c Migrators[" + players.size() + "]"));
				ArrayList<Pair<AbstractClientPlayerEntity, Double>> sorted = new ArrayList<>();
				for(Pair<AbstractClientPlayerEntity, Double> pair : players) for(int i = 0; i <= sorted.size(); i++) try {
					if(sorted.get(i).getRight() >= pair.getRight()) {
						sorted.add(i, pair);
						break;
					}
				}
				catch(IndexOutOfBoundsException e) {
					sorted.add(pair);
					break;
				}
				for(Pair<AbstractClientPlayerEntity, Double> pair : sorted){
					AbstractClientPlayerEntity player = pair.getLeft();
					float health = player.getHealth();
					double[] damage = {1, 0, 1, 0}; // base, add, multiply, hit
					for(EntityAttributeModifier mod : player.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
						if(mod.getOperation() == Operation.MULTIPLY_BASE) damage[0] *= mod.getValue();
						else if(mod.getOperation() == Operation.ADDITION) damage[1] += mod.getValue();
						else damage[2] *= mod.getValue();
					}
					damage[3] = (damage[0] + damage[1]) * damage[2];
					int armor = (int)player.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
					int prot = EnchantmentHelper.getEquipmentLevel(Enchantments.PROTECTION, player);
					int sharp = EnchantmentHelper.getEquipmentLevel(Enchantments.SHARPNESS, player);
					client.inGameHud.getChatHud().addMessage(new LiteralText("§4§l[\u0d9e>§r ").append(player.getDisplayName())
							.append(" §3<" + (int)(pair.getRight() * 10) / 10f + ">").append(" §9" + player.getBlockPos().toShortString())
							.append(" §7🛡" + armor + "§5" + prot).append(" §c\u2764" + health).append(" §4🗡" + damage[3] + "§5" + sharp));
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
