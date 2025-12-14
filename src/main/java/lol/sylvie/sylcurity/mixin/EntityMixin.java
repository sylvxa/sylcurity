package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow public abstract UUID getUUID();

	@Inject(method = "broadcastToPlayer", at = @At("HEAD"), cancellable = true)
	public void cameraVisibilityHook(ServerPlayer spectator, CallbackInfoReturnable<Boolean> cir) {
		if (CameraViewer.inCamera(getUUID()))
			cir.setReturnValue(false);
	}
}
