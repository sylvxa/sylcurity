package lol.sylvie.sylcurity.block;

import com.mojang.serialization.Codec;
import lol.sylvie.sylcurity.messaging.FormattingUtil;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import lol.sylvie.sylcurity.messaging.SecurityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class SecurityBlockEntity extends BlockEntity {
	public static final int MAX_NAME_LENGTH = 32;
	public static final int MAX_TAG_LENGTH = 16;
	public static final int MAX_GROUPS = 8;

	protected String name = FormattingUtil.pos(this.getPos());
	private UUID owner;
	protected HashMap<String, UUID> trusted = new HashMap<>();
	protected String channel = "";
	protected ArrayList<String> groups = new ArrayList<>();

	protected static Codec<List<String>> STRING_LIST_CODEC = Codec.STRING.listOf();
	protected static Codec<Map<String, UUID>> TRUSTED_CODEC = Codec.unboundedMap(Codec.STRING, Uuids.CODEC);

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
	protected void readData(ReadView view) {
		super.readData(view);

		view.getOptionalString("name").ifPresent(s -> name = s);
		view.read("owner", Uuids.CODEC).ifPresent(u -> owner = u);
		view.read("trusted", TRUSTED_CODEC).ifPresent(m -> trusted = new HashMap<>(m));
		view.getOptionalString("channel").ifPresent(s -> channel = s);
		view.read("groups", STRING_LIST_CODEC).ifPresent(g -> groups = new ArrayList<>(g));
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		if (name != null) view.putString("name", name);
		if (owner != null) view.put("owner", Uuids.CODEC, owner);
		view.put("trusted", TRUSTED_CODEC, trusted);
		if (channel != null) view.putString("channel", channel);
		view.put("groups", STRING_LIST_CODEC, groups);
	}

	@Override
	public void onBlockReplaced(BlockPos pos, BlockState oldState) {
		super.onBlockReplaced(pos, oldState);
		this.setChannel("");
	}

	public boolean checkAccess(UUID uuid) {
		return uuid.equals(this.getOwner()) || trusted.containsValue(uuid);
	}

	public boolean checkAccess(PlayerEntity player) {
		return checkAccess(player.getUuid());
	}

	public boolean checkAccessVisibly(PlayerEntity player) {
		if (this.getOwner() == null) {
			this.setOwner(player.getUuid());
			player.sendMessage(Text.translatable("menu.sylcurity.ownership_notice").formatted(Formatting.GREEN), true);
		}

		if (!this.checkAccess(player)) {
			player.sendMessage(Text.translatable("menu.sylcurity.access_error").formatted(Formatting.RED), true);
			return false;
		}
		return true;
	}
}
