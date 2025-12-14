package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin {
	// Entity interaction
	@Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
	public void cameraNoInteract(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		Entity thisAsEntity = (Entity) (Object) this;
		if (CameraViewer.inCamera(thisAsEntity.getUUID()))
			cir.setReturnValue(InteractionResult.FAIL);
	}

	@Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
	public void cameraNoDamage(CallbackInfoReturnable<Boolean> cir) {
		Entity thisAsEntity = (Entity) (Object) this;
		if (CameraViewer.inCamera(thisAsEntity.getUUID()))
			cir.setReturnValue(false);
	}
}
