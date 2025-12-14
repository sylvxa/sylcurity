package lol.sylvie.sylcurity.datagen.impl;

import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class SylcurityRecipeGenerator extends FabricRecipeProvider {
	public SylcurityRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
		return new RecipeProvider(wrapperLookup, recipeExporter) {
			@Override
			public void buildRecipes() {
				Item TERMINAL = ModBlocks.TERMINAL.asItem();
				shaped(RecipeCategory.MISC, TERMINAL, 1)
						.pattern("iii")
						.pattern("ibi")
						.pattern("igi")
						.define('i', Items.IRON_INGOT)
						.define('g', Items.GLASS_PANE)
						.define('b', Items.REDSTONE_BLOCK)
						.group(Sylcurity.MOD_ID)
						.unlockedBy(getHasName(TERMINAL), has(TERMINAL))
						.save(output);

				Item EVENT_RECEIVER = ModBlocks.EVENT_RECEIVER.asItem();
				shaped(RecipeCategory.MISC, EVENT_RECEIVER, 1)
						.pattern("iri")
						.pattern("ses")
						.pattern("sss")
						.define('i', Items.REDSTONE_TORCH)
						.define('s', Items.SMOOTH_STONE)
						.define('e', Items.ENDER_PEARL)
						.define('r', Items.REDSTONE)
						.group(Sylcurity.MOD_ID)
						.unlockedBy(getHasName(TERMINAL), has(TERMINAL))
						.save(output);

				Item PLAYER_DETECTOR = ModBlocks.PLAYER_DETECTOR.asItem();
				shaped(RecipeCategory.MISC, PLAYER_DETECTOR, 1)
						.pattern("rsr")
						.pattern("tbt")
						.pattern("iii")
						.define('i', Items.IRON_INGOT)
						.define('r', Items.REDSTONE_TORCH)
						.define('t', Items.TRIPWIRE_HOOK)
						.define('b', Items.REDSTONE_BLOCK)
						.define('s', Items.SCULK_SENSOR)
						.group(Sylcurity.MOD_ID)
						.unlockedBy(getHasName(TERMINAL), has(TERMINAL))
						.save(output);

				Item ACTIVITY_LOG = ModBlocks.ACTIVITY_LOG.asItem();
				shaped(RecipeCategory.MISC, ACTIVITY_LOG, 1)
						.pattern("ppp")
						.pattern("plp")
						.pattern("iii")
						.define('i', Items.IRON_INGOT)
						.define('l', Items.LECTERN)
						.define('p', Items.PAPER)
						.group(Sylcurity.MOD_ID)
						.unlockedBy(getHasName(TERMINAL), has(TERMINAL))
						.save(output);

				Item CAMERA = ModBlocks.CAMERA.asItem();
				shaped(RecipeCategory.MISC, CAMERA, 1)
						.pattern("iii")
						.pattern("ibi")
						.pattern("ege")
						.define('i', Items.IRON_INGOT)
						.define('e', Items.ENDER_PEARL)
						.define('g', Items.GLASS_PANE)
						.define('b', Items.REDSTONE_BLOCK)
						.group(Sylcurity.MOD_ID)
						.unlockedBy(getHasName(TERMINAL), has(TERMINAL))
						.save(output);
			}
		};
	}

	@Override
	public String getName() {
		return "SylcurityRecipeGenerator";
	}
}
