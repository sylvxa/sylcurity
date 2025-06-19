package lol.sylvie.sylcurity.datagen.impl;

import lol.sylvie.sylcurity.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class SylcurityBlockLootTableGenerator extends FabricBlockLootTableProvider {
	public SylcurityBlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {
		addDrop(ModBlocks.EVENT_RECEIVER);
		addDrop(ModBlocks.CAMERA);
		addDrop(ModBlocks.ACTIVITY_LOG);
		addDrop(ModBlocks.TERMINAL);
		addDrop(ModBlocks.PLAYER_DETECTOR);
	}
}
