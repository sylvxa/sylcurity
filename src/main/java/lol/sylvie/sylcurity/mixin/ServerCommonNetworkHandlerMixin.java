package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.gui.DialogHelper;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonNetworkHandlerMixin {
	@Inject(method = "handleCustomClickAction", at = @At("TAIL"))
	public void handleDialogLocationSelect(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
		if (!(((ServerCommonPacketListenerImpl) (Object) this instanceof ServerGamePacketListenerImpl playNetworkHandler))) return;

		DialogHelper.onCustomClickAction(packet, playNetworkHandler.player);
	}
}