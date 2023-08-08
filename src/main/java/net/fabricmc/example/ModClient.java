package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.example.event.KeyInputHandler;
import net.fabricmc.example.event.MigratorStatsScreen;

public class ModClient implements ClientModInitializer{

	@Override
	public void onInitializeClient() {
		KeyInputHandler.register();
		MigratorStatsScreen.registerStatsScreen();
	}
}