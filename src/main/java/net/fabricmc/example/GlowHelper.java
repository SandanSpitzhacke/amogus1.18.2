package net.fabricmc.example;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;

public class GlowHelper{
	
	//skins in 1.18, capes in 1.19?
	private static final String CAPEID = "minecraft:skins/17f76a23ff4d227a94ea3d5802dccae9f2ae9aa9";
	
	// required to be public for unicode
	public static TextFieldWidget unicodeuser = null;
	public static int[] linepos = {0, 0, 0}; //x1, x2, y
//	public static TextRenderer renderer;
	
	public static boolean isMigrator(AbstractClientPlayerEntity player){
		if(player.isPartVisible(PlayerModelPart.CAPE)) if(player.getCapeTexture() != null)
			return player.getCapeTexture().toString().equals(CAPEID);
		return false;
	}
}
