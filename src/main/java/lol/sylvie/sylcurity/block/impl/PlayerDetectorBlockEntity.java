package lol.sylvie.sylcurity.block.impl;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import lol.sylvie.sylcurity.messaging.SecurityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PlayerDetectorBlockEntity extends SecurityBlockEntity {
	public ServerPlayer lastPlayer = null;

	public PlayerDetectorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PLAYER_DETECTOR_BLOCK_ENTITY, pos, state);
	}

	private static Vec3 positionVector(float yaw) {
		float g = -yaw * ((float) Math.PI / 180F);
		float h = Mth.cos(g);
		float i = Mth.sin(g);
		return new Vec3(i, 0, h);
	}

	private static void broadcast(PlayerDetectorBlockEntity entity, ServerPlayer offender) {
		entity.groups.forEach(g -> SecurityRegistry.post(new SecurityMessage.PlayerDetection(entity.channel, g, entity.getName(), offender), entity.getOwner()));
	}

	private static final double MAX_DIST = 32;
	public static void tick(Level world, BlockPos blockPos, BlockState blockState, PlayerDetectorBlockEntity entity) {
		int rotation = blockState.getValue(BlockStateProperties.ROTATION_16);
		float yaw = RotationSegment.convertToDegrees(rotation) - 180;
		Vec3 minPos = blockPos.getCenter().add(0, -.25, 0);
		Vec3 vector = positionVector(yaw);
		Vec3 maxPos = minPos.add(vector.scale(MAX_DIST));

		AABB volume = AABB.encapsulatingFullBlocks(blockPos, new BlockPos((int) maxPos.x, (int) maxPos.y, (int) maxPos.z));
		EntityHitResult result = ProjectileUtil.getEntityHitResult(new FakeEntity(world), minPos.add(vector.scale(0.3)), maxPos, volume, test -> test.isAlwaysTicking() && !test.isSpectator() && !entity.checkAccess(test.getUUID()), MAX_DIST);
		if (result == null || result.getEntity() == null) {
			if (entity.lastPlayer != null) {
				entity.lastPlayer = null;
			}
			return;
		}

		Entity resultEntity = result.getEntity();
		if (resultEntity instanceof ServerPlayer player && player != entity.lastPlayer) {
			broadcast(entity, player);
			entity.lastPlayer = player;
		}
	}

	private static class FakeEntity extends Entity {
		public FakeEntity(Level world) {
			super(EntityType.ARROW, world);
		}

		@Override
		protected void defineSynchedData(SynchedEntityData.Builder builder) {}

		@Override
		public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
			return false;
		}

		@Override
		protected void readAdditionalSaveData(ValueInput view) {}

		@Override
		protected void addAdditionalSaveData(ValueOutput view) {}
	}
}
