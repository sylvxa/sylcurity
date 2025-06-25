package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
		AbstractFurnaceScreenHandler.class,
		BeaconScreenHandler.class,
		BrewingStandScreenHandler.class,
		CartographyTableScreenHandler.class,
		CrafterScreenHandler.class,
		EnchantmentScreenHandler.class,
		ForgingScreenHandler.class,
		Generic3x3ContainerScreenHandler.class,
		GenericContainerScreenHandler.class,
		GrindstoneScreenHandler.class,
		HopperScreenHandler.class,
		HorseScreenHandler.class,
		LecternScreenHandler.class,
		LoomScreenHandler.class,
		MerchantScreenHandler.class,
		ShulkerBoxScreenHandler.class,
		StonecutterScreenHandler.class
})
public class ScreenHandlerMixin {
	@Inject(method = "canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
	public void noCameraInteract(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		if (CameraViewer.inCamera(player.getUuid()))
			cir.setReturnValue(false);
	}
}
