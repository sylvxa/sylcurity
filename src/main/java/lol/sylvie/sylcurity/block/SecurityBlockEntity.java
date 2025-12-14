package lol.sylvie.sylcurity.block;

import com.mojang.serialization.Codec;
import lol.sylvie.sylcurity.messaging.FormattingUtil;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import lol.sylvie.sylcurity.messaging.SecurityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.*;

public class SecurityBlockEntity extends BlockEntity {
	public static final int MAX_NAME_LENGTH = 32;
	public static final int MAX_TAG_LENGTH = 16;
	public static final int MAX_GROUPS = 8;

	protected String name = FormattingUtil.pos(this.getBlockPos());
	private UUID owner;
	protected HashMap<String, UUID> trusted = new HashMap<>();
	protected String channel = "";
	protected ArrayList<String> groups = new ArrayList<>();

	protected static Codec<List<String>> STRING_LIST_CODEC = Codec.STRING.listOf();
	protected static Codec<Map<String, UUID>> TRUSTED_CODEC = Codec.unboundedMap(Codec.STRING, UUIDUtil.AUTHLIB_CODEC);

	public SecurityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public HashMap<String, UUID> getTrusted() {
		return trusted;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		if (SecurityRegistry.REGISTRY.containsKey(this.channel))
			SecurityRegistry.REGISTRY.get(this.channel).remove(this);
		this.channel = channel;
		SecurityRegistry.REGISTRY.computeIfAbsent(this.channel, (c) -> new ArrayList<>()).add(this);
	}

	public ArrayList<String> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}

	public void accept(SecurityMessage message) {};

	@Override
	protected void loadAdditional(ValueInput view) {
		super.loadAdditional(view);

		view.getString("name").ifPresent(s -> name = s);
		view.read("owner", UUIDUtil.AUTHLIB_CODEC).ifPresent(u -> owner = u);
		view.read("trusted", TRUSTED_CODEC).ifPresent(m -> trusted = new HashMap<>(m));
		view.getString("channel").ifPresent(s -> channel = s);
		view.read("groups", STRING_LIST_CODEC).ifPresent(g -> groups = new ArrayList<>(g));
	}

	@Override
	protected void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);

		if (name != null) view.putString("name", name);
		if (owner != null) view.store("owner", UUIDUtil.AUTHLIB_CODEC, owner);
		view.store("trusted", TRUSTED_CODEC, trusted);
		if (channel != null) view.putString("channel", channel);
		view.store("groups", STRING_LIST_CODEC, groups);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
		super.preRemoveSideEffects(pos, oldState);
		this.setChannel("");
	}

	public boolean checkAccess(UUID uuid) {
		return uuid.equals(this.getOwner()) || trusted.containsValue(uuid);
	}

	public boolean checkAccess(Player player) {
		return checkAccess(player.getUUID());
	}

	public boolean checkAccessVisibly(Player player) {
		if (this.getOwner() == null) {
			this.setOwner(player.getUUID());
			player.displayClientMessage(Component.translatable("menu.sylcurity.ownership_notice").withStyle(ChatFormatting.GREEN), true);
		}

		if (!this.checkAccess(player)) {
			player.displayClientMessage(Component.translatable("menu.sylcurity.access_error").withStyle(ChatFormatting.RED), true);
			return false;
		}
		return true;
	}
}
