package lol.sylvie.sylcurity.datagen.impl;

import lol.sylvie.sylcurity.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class SylcurityBlockLootTableGenerator extends FabricBlockLootTableProvider {
	public SylcurityBlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {
		dropSelf(ModBlocks.EVENT_RECEIVER);
		dropSelf(ModBlocks.CAMERA);
		dropSelf(ModBlocks.ACTIVITY_LOG);
		dropSelf(ModBlocks.TERMINAL);
		dropSelf(ModBlocks.PLAYER_DETECTOR);
	}
}
