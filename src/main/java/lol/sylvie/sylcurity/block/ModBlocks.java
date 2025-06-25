package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerHeadBlockItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import lol.sylvie.sylcurity.Sylcurity;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import lol.sylvie.sylcurity.block.impl.ActivityLogBlock;
import lol.sylvie.sylcurity.block.impl.EventReceiverBlock;
import lol.sylvie.sylcurity.block.impl.PlayerDetectorBlock;
import lol.sylvie.sylcurity.block.impl.camera.CameraBlock;
import lol.sylvie.sylcurity.block.impl.TerminalBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Function;

public class ModBlocks {
    public static final Block CAMERA = register(
            "camera",
            CameraBlock::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).strength(5, 9).solid(),
            Items.PLAYER_HEAD
    );

    public static final Block TERMINAL = register(
            "terminal",
            TerminalBlock::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).strength(5, 9).solid(),
            Items.PLAYER_HEAD
    );

    public static final Block ACTIVITY_LOG = register(
            "activity_log",
            ActivityLogBlock::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).strength(5, 9).solid(),
            Items.PLAYER_HEAD
    );

    public static final Block EVENT_RECEIVER = register(
            "event_receiver",
            EventReceiverBlock::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).strength(5, 9).solid(),
            Items.LODESTONE
    );


    public static final Block PLAYER_DETECTOR = register(
            "player_detector",
            PlayerDetectorBlock::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).strength(5, 9).solid(),
            Items.PLAYER_HEAD
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, Item polymerItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (polymerItem != null) {
            RegistryKey<Item> itemKey = keyOfItem(name);

            Item.Settings itemSettings = new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey();

            BlockItem blockItem = getBlockItem(polymerItem, block, itemSettings);

            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static @NotNull BlockItem getBlockItem(Item polymerItem, Block block, Item.Settings itemSettings) {
        BlockItem blockItem;
        if (block instanceof PolymerHeadBlock head)
            blockItem = new PolymerHeadBlockItem((Block & PolymerHeadBlock) head, itemSettings);
        else blockItem = new PolymerBlockItem(block, itemSettings, polymerItem, true) {
            @Override
            public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
                return PolymerResourcePackUtils.hasMainPack(context) ? super.getPolymerItemModel(stack, context) : null;
            }
        };
        return blockItem;
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Sylcurity.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Sylcurity.MOD_ID, name));
    }

    public static void initialize() {}

}