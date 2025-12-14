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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import lol.sylvie.sylcurity.block.impl.TerminalBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Function;

public class ModBlocks {
    public static final Block CAMERA = register(
            "camera",
            CameraBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.IRON).strength(5, 9).forceSolidOn(),
            Items.PLAYER_HEAD
    );

    public static final Block TERMINAL = register(
            "terminal",
            TerminalBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.IRON).strength(5, 9).forceSolidOn(),
            Items.PLAYER_HEAD
    );

    public static final Block ACTIVITY_LOG = register(
            "activity_log",
            ActivityLogBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.IRON).strength(5, 9).forceSolidOn(),
            Items.PLAYER_HEAD
    );

    public static final Block EVENT_RECEIVER = register(
            "event_receiver",
            EventReceiverBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.IRON).strength(5, 9).forceSolidOn(),
            Items.LODESTONE
    );


    public static final Block PLAYER_DETECTOR = register(
            "player_detector",
            PlayerDetectorBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.IRON).strength(5, 9).forceSolidOn(),
            Items.PLAYER_HEAD
    );

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, Item polymerItem) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockKey));

        if (polymerItem != null) {
            ResourceKey<Item> itemKey = keyOfItem(name);

            Item.Properties itemSettings = new Item.Properties().setId(itemKey).useBlockDescriptionPrefix();

            BlockItem blockItem = getBlockItem(polymerItem, block, itemSettings);

            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static @NotNull BlockItem getBlockItem(Item polymerItem, Block block, Item.Properties itemSettings) {
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

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, name));
    }

    public static void initialize() {}

}