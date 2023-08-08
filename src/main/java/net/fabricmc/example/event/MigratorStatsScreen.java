package net.fabricmc.example.event;

import java.util.ArrayList;
import java.util.HashMap;

import net.fabricmc.example.GlowHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class MigratorStatsScreen{
	private static final String[] STAT_ORDER = new String[]{"+name", "x", "y", "z", "distance", "health", "damage", "+sharp", "armor", "+prot"};
	
	public static void registerStatsScreen(){
		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
			// hotbar is 182x22, 1 line is 9px high
			// spacing: 3-4px to hotbar, 4px to bottom
			// text width: renderer.getWidth()
			if(!KeyInputHandler.display) return;
			MinecraftClient client = MinecraftClient.getInstance();
			if(client.options.guiScale == 0) return; // auto
			TextRenderer renderer = client.textRenderer;
			int[] screen = {client.getWindow().getWidth() / client.options.guiScale, client.getWindow().getHeight() / client.options.guiScale};
			ArrayList<Pair<AbstractClientPlayerEntity, Double>> players = getPlayersByDistance(client);
			ArrayList<HashMap<String, InfoPart<Object>>> stats = new ArrayList<>();
			int x = screen[0] / 2 + 99; // half hotbar + 4px before name
			int y = screen[1] - 22;
			for(Pair<? extends PlayerEntity, ?> player : players) stats.add(getPlayerInfo(player.getLeft(), client));
			for(String i : STAT_ORDER){
				boolean alignright = i.charAt(0) != '+'; 
				if(i.charAt(0) == '+') i = i.substring(1);
				else x += 4;
				String title = I18n.translate("stat.amogus." + i);
				int maxwidth = renderer.getWidth(title);
				for(HashMap<String, InfoPart<Object>> info : stats) maxwidth = Math.max(maxwidth, info.get(i).pixelsize);
				if(!alignright) renderer.drawWithShadow(matrixStack, title, x, screen[1] - 10, 0xffffffff);
				else renderer.drawWithShadow(matrixStack, title, x + maxwidth - renderer.getWidth(title), screen[1] - 10, -1);
				for(int player = 0; player < stats.size(); player++){
					InfoPart<Object> info = stats.get(player).get(i);
					int rx = x + (alignright ? (maxwidth - (info.value instanceof Text ? renderer.getWidth((Text)info.value) : renderer.getWidth(info.value.toString()))) : 0);
					if(info.value instanceof Text) renderer.drawWithShadow(matrixStack, (Text)info.value, rx, y - 9 * player, info.color);
					else renderer.drawWithShadow(matrixStack, info.value.toString(), rx, y - 9 * player, info.color);
				}
				x += maxwidth;
			}
			DrawableHelper.fill(matrixStack, screen[0] / 2 + 96, screen[1] - 13, x + 3, screen[1] - 12, 0xffffffff);
		});
	}
	
	public static ArrayList<Pair<AbstractClientPlayerEntity, Double>> getPlayersByDistance(MinecraftClient client){
		ArrayList<Pair<AbstractClientPlayerEntity, Double>> players = new ArrayList<>();
		for(AbstractClientPlayerEntity player : client.world.getPlayers()) if(!GlowHelper.isMigrator(player))
			players.add(new Pair<>(player, player.getPos().distanceTo(client.player.getPos())));
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
		return sorted;
	}
	
	public static ArrayList<Text> getMigratorStats(MinecraftClient client){
		ArrayList<Text> result = new ArrayList<>();
		ArrayList<Pair<AbstractClientPlayerEntity, Double>> sorted = getPlayersByDistance(client);
		for(Pair<AbstractClientPlayerEntity, Double> pair : sorted){
			AbstractClientPlayerEntity player = pair.getLeft();
			float health = player.getHealth();
			double[] damage = {1, 0, 1, 0}; // base, add, multiply, hit
			if(player.hasStatusEffect(StatusEffects.STRENGTH)) damage[1] += player.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() * 3 + 3;
			for(EntityAttributeModifier mod : player.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
				if(mod.getOperation() == Operation.MULTIPLY_BASE) damage[0] *= mod.getValue();
				else if(mod.getOperation() == Operation.ADDITION) damage[1] += mod.getValue();
				else damage[2] *= mod.getValue();
			}
			damage[3] = (damage[0] + damage[1]) * damage[2];
			int armor = (int)player.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
			int prot = EnchantmentHelper.getEquipmentLevel(Enchantments.PROTECTION, player);
			int sharp = EnchantmentHelper.getEquipmentLevel(Enchantments.SHARPNESS, player);
			result.add(new LiteralText("§4§l[\u0d9e>§r ").append(player.getDisplayName())
					.append(" §3<" + (int)(pair.getRight() * 10) / 10f + ">").append(" §9" + player.getBlockPos().toShortString())
					.append(" §7🛡" + armor + "§5" + prot).append(" §c\u2764" + health).append(" §4🗡" + damage[3] + "§5" + sharp));
		}
		return result;
	}
	
	public static HashMap<String, InfoPart<Object>> getPlayerInfo(PlayerEntity player, MinecraftClient client){
		HashMap<String, InfoPart<Object>> info = new HashMap<>();
		info.put("name", new InfoPart<>(player.getDisplayName(), client.textRenderer));
		BlockPos position = player.getBlockPos();
		info.put("x", new InfoPart<>(position.getX(), client.textRenderer, 0xff209070));
		info.put("y", new InfoPart<>(position.getY(), client.textRenderer, 0xff209070));
		info.put("z", new InfoPart<>(position.getZ(), client.textRenderer, 0xff209070));
		info.put("distance", new InfoPart<>((int)(player.distanceTo(client.player)*10)/10f, client.textRenderer, 0xffbf7f00));
		info.put("health", new InfoPart<>((int)(player.getHealth()*10)/10f, client.textRenderer, 0xffbb0000));
		double[] damage = {1, 0, 1, 0}; // base, add, multiply, hit
		if(player.hasStatusEffect(StatusEffects.STRENGTH)) damage[1] += player.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() * 3 + 3;
		for(EntityAttributeModifier mod : player.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
			if(mod.getOperation() == Operation.MULTIPLY_BASE) damage[0] *= mod.getValue();
			else if(mod.getOperation() == Operation.ADDITION) damage[1] += mod.getValue();
			else damage[2] *= mod.getValue();
		}
		damage[3] = (damage[0] + damage[1]) * damage[2];
		info.put("damage", new InfoPart<>((float)damage[3], client.textRenderer, 0xff802020));
		info.put("armor", new InfoPart<>((int)player.getAttributeValue(EntityAttributes.GENERIC_ARMOR), client.textRenderer, 0xff80bbff));
		info.put("sharp", new InfoPart<>(EnchantmentHelper.getEquipmentLevel(Enchantments.SHARPNESS, player), client.textRenderer, 0xff8020d0));
		info.put("prot", new InfoPart<>(EnchantmentHelper.getEquipmentLevel(Enchantments.PROTECTION, player), client.textRenderer, 0xff8030ff));
		return info;
	}
	
	public static class InfoPart<T>{
		T value;
		int pixelsize;
		int color;
		
		public InfoPart(T value, TextRenderer renderer){
			this(value, renderer, 0xffffffff);
		}
		
		public InfoPart(T value, TextRenderer renderer, int color){
			this.value = value;
			this.color = color;
			if(value instanceof Text) pixelsize = renderer.getWidth(((Text)value).asOrderedText());
			else pixelsize = renderer.getWidth(value.toString());
		}
	}
}
