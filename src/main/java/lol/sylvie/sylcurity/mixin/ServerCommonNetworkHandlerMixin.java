package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.gui.DialogHelper;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin {
	@Inject(method = "onCustomClickAction", at = @At("TAIL"))
	public void handleDialogLocationSelect(CustomClickActionC2SPacket packet, CallbackInfo ci) {
		if (!(((ServerCommonNetworkHandler) (Object) this instanceof ServerPlayNetworkHandler playNetworkHandler))) return;

		DialogHelper.onCustomClickAction(packet, playNetworkHandler.player);
	}
}