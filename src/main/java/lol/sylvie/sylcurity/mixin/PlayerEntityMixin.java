package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	// Entity interaction
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void cameraNoInteract(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		Entity thisAsEntity = (Entity) (Object) this;
		if (CameraViewer.inCamera(thisAsEntity.getUuid()))
			cir.setReturnValue(ActionResult.FAIL);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	public void cameraNoDamage(CallbackInfoReturnable<Boolean> cir) {
		Entity thisAsEntity = (Entity) (Object) this;
		if (CameraViewer.inCamera(thisAsEntity.getUuid()))
			cir.setReturnValue(false);
	}
}
