package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.example.GlowHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(TextFieldWidget.class)
public abstract class TextUnicodeMixin {
	
//	private static boolean unicodemode = false;
	private static int code = 0;
	private static int lastrun = -1;
//	private static boolean run = false;
//	private static int[] linepos = {0, 0};
	
	@Shadow @Final
	private TextRenderer textRenderer;
	
	@Shadow
	private int focusedTicks;
	
	@Shadow
	private boolean drawsBackground;
	
	@Shadow
	private int firstCharacterIndex;
	
	@Shadow
	private boolean selecting;
	
	@Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
	public void keyPressed(int keycode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> info){
		if(lastrun == focusedTicks) return;
		lastrun = focusedTicks;
		TextFieldWidget widget = ((TextFieldWidget)(Object)this);
		if(GlowHelper.unicodeuser == widget){
			try {
				code *= 16;
				code += Integer.parseInt(String.valueOf((char)keycode), 16);
				int chars = Integer.toString(code, 16).length();
				System.out.println("code is " + code + ", chars are " + chars);
				GlowHelper.linepos[0] = textRenderer.getWidth(widget.getText().substring(firstCharacterIndex, widget.getCursor() - chars)) + (drawsBackground ? 3 : -1);
				GlowHelper.linepos[1] = textRenderer.getWidth(widget.getText().substring(firstCharacterIndex, widget.getCursor()) + (char)keycode) + (drawsBackground ? 4 : 0);
			}
			catch(NumberFormatException e){
				// move cursor back, auto-moves to the end
				String text = widget.getText();
				int chars = Integer.toString(code, 16).length(), cursor = widget.getCursor();
				widget.setText(text.substring(0, widget.getCursor() - chars) + (char)(code/16) + text.substring(widget.getCursor()));
				selecting = false;
				widget.setCursor(cursor - chars + 1);
				GlowHelper.unicodeuser = null;
				code = 0;
			}
			info.setReturnValue(true);
			info.cancel();
		}
		if(keycode == 85 && modifiers == 3){
			widget.charTyped('u', 0);
			code = 0;
			GlowHelper.linepos[0] = textRenderer.getWidth(widget.getText().substring(firstCharacterIndex, widget.getCursor() - 1)) + (drawsBackground ? 3 : -1);
			GlowHelper.linepos[1] = textRenderer.getWidth(widget.getText().substring(firstCharacterIndex, widget.getCursor())) + (drawsBackground ? 4 : 0);
			GlowHelper.linepos[2] = widget.y - 1 + textRenderer.fontHeight + (drawsBackground ? widget.getHeight() / 2 - 4 : 0);
			GlowHelper.unicodeuser = widget;
		}
	}
	
	@Mixin(ClickableWidget.class)
	private static class UnicodeUnderlineMixin{
		@Inject(method = "render", at = @At("TAIL"))
		public void render(MatrixStack stack, int x, int y, float delta, CallbackInfo info){
			// text is centered
			if(!((Object)this instanceof TextFieldWidget)) return;
			ClickableWidget widget = (TextFieldWidget)(Object)this;
			if(!(GlowHelper.unicodeuser == widget)) return;
			DrawableHelper.fill(stack, widget.x + GlowHelper.linepos[0], GlowHelper.linepos[2], widget.x + GlowHelper.linepos[1], GlowHelper.linepos[2] + 1, 0xffffffff);
		}
	}
}
