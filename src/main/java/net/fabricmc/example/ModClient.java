package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.example.event.KeyInputHandler;

public class ModClient implements ClientModInitializer{

	@Override
	public void onInitializeClient() {
		KeyInputHandler.register();
	}
}