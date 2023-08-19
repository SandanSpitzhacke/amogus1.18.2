package net.fabricmc.example;

import org.apache.commons.lang3.function.TriFunction;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Config(name = "amogus")
public class ModConfig implements ConfigData{
	
	public static ModConfig get(){
		return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}
	
	public boolean invertAlt = false;
	@ConfigEntry.Gui.CollapsibleObject
	public NumberOption gamma = new NumberOption(1, 5, 1);
	@ConfigEntry.Gui.CollapsibleObject
	public NumberOption fov = new NumberOption(30, 110, 10);
	@ConfigEntry.Gui.CollapsibleObject
	public LineOption lines = new LineOption();
	@ConfigEntry.Gui.CollapsibleObject
	public GlowOption glow = new GlowOption();
	
	public boolean getToggle() {
		return invertAlt != Screen.hasAltDown();
	}

	public static class NumberOption{
		public int min, avg, max, shift, current;
		boolean nearest = false;

		public NumberOption(int min, int max, int shift){
			this.min = min;
			this.max = max;
			this.avg = (min + max) / 2;
			this.shift = shift;
			this.current = this.max;
		}
		
		public int getToggled(int current){
			return (current < avg) != (nearest && current != max && current != min) ? max : min;
		}
		
		public int getShifted(int current, boolean up){
			return current + shift * (up ? 1 : -1);
		}
		
		public void setToggled(){
			current = getToggled(current);
		}
		
		public void setShifted(boolean up) {
			current = getShifted(current, up);
		}
	}

	public static class LineOption{
		@ConfigEntry.Gui.Excluded
		public Color color1, color2;
		public String colorstr1 = "bf1f00", colorstr2 = "5f007f";
		public Gradient gradient = Gradient.RGB;
		public int mindist = 0, maxdist = 50;
		public boolean active = true;
		public int argbAt(float distance){
			if(maxdist == mindist) return color1.toargb();
			return gradient.get(color1, color2, (Math.max(1, distance) - mindist) / (maxdist - mindist));
		}
	}
	
	public static class GlowOption{
		@ConfigEntry.Gui.Excluded
		public Color color;
		public String colorstr = "bf1f00";
		public boolean overrideColor = true;
		public boolean active = true;
	}
	
	@Override
	public void validatePostLoad() throws ValidationException{
		applyMenuChanges();
		ConfigData.super.validatePostLoad();
	}
	
	public void applyMenuChanges(){
		MinecraftClient client = MinecraftClient.getInstance();
		lines.color1 = Color.fromString(lines.colorstr1);
		lines.color2 = Color.fromString(lines.colorstr2);
		glow.color = Color.fromString(glow.colorstr);
		if(client.options != null) client.options.fov = fov.current;
	}
	
	public void applyGameChanges(){
		MinecraftClient client = MinecraftClient.getInstance();
		fov.current = (int)client.options.fov;
	}
	
	public static enum Gradient{
		RGB((color2, color1, pos) -> {
			if(color1 == color2) return color1.toargb();
			int r = (int) (color1.r * pos + color2.r * (1 - pos));
			int g = (int) (color1.g * pos + color2.g * (1 - pos));
			int b = (int) (color1.b * pos + color2.b * (1 - pos));
			int a = (int) (color1.a * pos + color2.a * (1 - pos));
			return (a << 24) + (r << 16) + (g << 8) + b;
		}), 
		HSV((color2, color1, pos) -> {
			if(color1 == color2) return color1.toargb();
			double h = color1.h * pos + color2.h * (1 - pos);
			double s = color1.s * pos + color2.s * (1 - pos);
			double v = color1.v * pos + color2.v * (1 - pos);
			int a = (short)(color1.a * pos + color2.a * (1 - pos));
			short[] rgb = Color.hsvtorgb(h, s, v);
			return (a << 24) + (rgb[0] << 16) + (rgb[1] << 8) + rgb[2];
		});
		
		TriFunction<Color, Color, Float, Integer> function;
		Gradient(TriFunction<Color, Color, Float, Integer> function){
			this.function = function;
		}
		public int get(Color color1, Color color2, float pos){
			return function.apply(color1, color2, pos);
		}
	}
	
	public static class Color{
		// shorts used instead of bytes to prevent signs causing problems
		short r, g, b, a;
		double h, s, v;
		Color(short red, short green, short blue, short alpha){
			r = red; g = green; b = blue; a = alpha;
			v = Math.max(Math.max(r, g), b);
			s = 1 - Math.min(Math.min(r, g), b) / v;
			v /= 255;
			h = Math.acos((r - 0.5 * g - 0.5 * b) / Math.sqrt(r * r + g * g + b * b - r * g - r * b - g * b));
			if(Double.isNaN(h)) h = 0;
			if(b > g) h = 2 * Math.PI - h;
		}
		Color(double hue, double sat, double value, byte alpha){
			h = hue; s = sat; v = value; a = alpha;
			short[] rgb = hsvtorgb(h, s, v);
			r = rgb[0]; g = rgb[1]; b = rgb[2];
		}
		
		public static Color fromString(String str){
			if(str.length() == 6) str += "ff";
			try{
				int colorint = (int)Long.parseLong(str, 16); // accept overflow
				short red = (short)(colorint >>> 24);
				short green = (short)(colorint << 8 >>> 24);
				short blue = (short)(colorint << 16 >>> 24);
				short alpha = (short)(colorint << 24 >>> 24);
				return new Color(red, green, blue, alpha);
			}
			catch(NumberFormatException e){
				return new Color((short)0xbf, (short)0x1f, (short)0x00, (short)0xff);
			}
		}
		
		public static short[] hsvtorgb(double hue, double sat, double value){
			double M = 255 * value, m = M * (1 - sat);
			int hueindex = (int) Math.min(3 * hue / Math.PI, 5);
			double z = (M - m) * (1 - Math.abs((3 * hue / Math.PI) % 2 - 1));
			short[] parts = {(short)M, (short)(z + m), (short)m};
			short r = parts[hueindex < 3 ? hueindex : 5 - hueindex];
			short g = parts[hueindex % 3 == 0 ? 1 : hueindex > 3 ? 2 : 0];
			short b = parts[hueindex < 2 ? 2 : hueindex > 3 ? hueindex - 4 : 4 - hueindex];
			return new short[]{r, g, b};
		}
		
		public int toargb(){
			return ((int)a << 24) + ((int)r << 16) + ((int)g << 8) + b;
		}
	}
}
