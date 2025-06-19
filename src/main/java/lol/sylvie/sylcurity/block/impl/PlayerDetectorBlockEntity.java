package lol.sylvie.sylcurity.block.impl;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import lol.sylvie.sylcurity.messaging.SecurityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class PlayerDetectorBlockEntity extends SecurityBlockEntity {
	public ServerPlayerEntity lastPlayer = null;

	public PlayerDetectorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PLAYER_DETECTOR_BLOCK_ENTITY, pos, state);
	}

	private static Vec3d positionVector(float yaw) {
		float g = -yaw * ((float) Math.PI / 180F);
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		return new Vec3d(i, 0, h);
	}

	private static void broadcast(PlayerDetectorBlockEntity entity, ServerPlayerEntity offender) {
		entity.groups.forEach(g -> SecurityRegistry.post(new SecurityMessage.PlayerDetection(entity.channel, g, entity.getName(), offender), entity.getOwner()));
	}

	private static final double MAX_DIST = 32;
	public static void tick(World world, BlockPos blockPos, BlockState blockState, PlayerDetectorBlockEntity entity) {
		int rotation = blockState.get(Properties.ROTATION);
		float yaw = RotationPropertyHelper.toDegrees(rotation) - 180;
		Vec3d minPos = blockPos.toCenterPos().add(0, -.25, 0);
		Vec3d vector = positionVector(yaw);
		Vec3d maxPos = minPos.add(vector.multiply(MAX_DIST));

		Box volume = Box.enclosing(blockPos, new BlockPos((int) maxPos.x, (int) maxPos.y, (int) maxPos.z));
		EntityHitResult result = ProjectileUtil.raycast(new FakeEntity(world), minPos.add(vector.multiply(0.3)), maxPos, volume, test -> test.isPlayer() && !test.isSpectator() && !entity.checkAccess(test.getUuid()), MAX_DIST);
		if (result == null || result.getEntity() == null) {
			if (entity.lastPlayer != null) {
				entity.lastPlayer = null;
			}
			return;
		}

		Entity resultEntity = result.getEntity();
		if (resultEntity instanceof ServerPlayerEntity player && player != entity.lastPlayer) {
			broadcast(entity, player);
			entity.lastPlayer = player;
		}
	}

	private static class FakeEntity extends Entity {
		public FakeEntity(World world) {
			super(EntityType.ARROW, world);
		}

		@Override
		protected void initDataTracker(DataTracker.Builder builder) {}

		@Override
		public boolean damage(ServerWorld world, DamageSource source, float amount) {
			return false;
		}

		@Override
		protected void readCustomData(ReadView view) {}

		@Override
		protected void writeCustomData(WriteView view) {}
	}
}
