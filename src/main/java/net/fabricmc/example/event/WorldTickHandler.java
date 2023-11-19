package net.fabricmc.example.event;

import net.fabricmc.example.GlowHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;


public class WorldTickHandler {
	public static void register() {
		// start of projectile dodging
		ClientTickEvents.START_WORLD_TICK.register(world -> {
			MinecraftClient client = MinecraftClient.getInstance();
			BlockPos pos = client.player.getBlockPos();
			for(Entity e : world.getOtherEntities(client.player, new Box(pos.add(-10, -10, -10), pos.add(10, 10, 10))))
				if(e instanceof ProjectileEntity && ((ProjectileEntity)e).getOwner() instanceof AbstractClientPlayerEntity)
					if(GlowHelper.isMigrator((AbstractClientPlayerEntity)((ProjectileEntity)e).getOwner()));
//			client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
//			ClientPlayNetworking.send(channelName, buf);
//				client.inGameHud.getChatHud().addMessage(Text.of("Entity " + e.getEntityName() + " is close at " + e.getBlockPos()));
		});
	}
}
