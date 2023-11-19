package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.example.GlowHelper;
import net.fabricmc.example.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Mixin(EntityRenderDispatcher.class)
public class MigratorLinesMixin{
	@Inject(at = @At(value="HEAD"), method = "shouldRender", cancellable = true)
	public <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> info){
		if(ModConfig.get().lines.active && entity instanceof AbstractClientPlayerEntity && GlowHelper.isMigrator((AbstractClientPlayerEntity)entity)) info.setReturnValue(true);
	}
	
	
	@Inject(at = @At(value="INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"), method = "render")
	<E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertex, int light, CallbackInfo info){
		if(!ModConfig.get().lines.active) return;
		if(!(entity instanceof AbstractClientPlayerEntity)) return;
		if(!GlowHelper.isMigrator((AbstractClientPlayerEntity)entity)) return;
		MinecraftClient client = MinecraftClient.getInstance();
		Vec3d line = client.cameraEntity.getClientCameraPosVec(tickDelta).add(client.cameraEntity.getRotationVec(tickDelta).multiply(0.99)).subtract(entity.getClientCameraPosVec(tickDelta));
		VertexConsumer buffer = vertex.getBuffer(RenderLayer.getLines());
		Matrix4f pos = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();
		int color = ModConfig.get().lines.argbAt((float)line.length());
		buffer.vertex(pos, 0f, entity.getStandingEyeHeight(), 0f).color(color).normal(normal, (float)line.x, (float)line.y, (float)line.z).next();
		buffer.vertex(pos, (float)line.x, (float)line.y + 1*entity.getStandingEyeHeight(), (float)line.z).color(color).normal(normal, (float)line.x, (float)line.y, (float)line.z).next();
	}
}