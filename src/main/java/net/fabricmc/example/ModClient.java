package net.fabricmc.example;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.example.event.KeyInputHandler;
import net.fabricmc.example.event.MigratorStatsScreen;

public class ModClient implements ClientModInitializer{
	@Override
	public void onInitializeClient() {
		KeyInputHandler.register();
		MigratorStatsScreen.registerStatsScreen();
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
	}
}