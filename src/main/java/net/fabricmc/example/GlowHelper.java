package net.fabricmc.example;

import net.fabricmc.example.event.KeyInputHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;

public class GlowHelper{
	
	//skins in 1.18, capes in 1.19?
	private static final String CAPEID = "minecraft:skins/17f76a23ff4d227a94ea3d5802dccae9f2ae9aa9";
	
	public static boolean isMigrator(AbstractClientPlayerEntity player){
		if(!KeyInputHandler.glowing) return false;
		if(player.isPartVisible(PlayerModelPart.CAPE)) if(player.getCapeTexture() != null) {
//			System.out.println("cape texture is" + player.getCapeTexture().toString() + ", returning " + player.getCapeTexture().toString().equals(CAPEID));
			return player.getCapeTexture().toString().equals(CAPEID);
		}
		return false;
	}
}
