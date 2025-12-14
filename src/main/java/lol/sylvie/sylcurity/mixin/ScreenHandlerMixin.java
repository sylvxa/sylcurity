package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
		AbstractFurnaceMenu.class,
		BeaconMenu.class,
		BrewingStandMenu.class,
		CartographyTableMenu.class,
		CrafterMenu.class,
		EnchantmentMenu.class,
		ItemCombinerMenu.class,
		DispenserMenu.class,
		ChestMenu.class,
		GrindstoneMenu.class,
		HopperMenu.class,
		LecternMenu.class,
		LoomMenu.class,
		MerchantMenu.class,
		ShulkerBoxMenu.class,
		StonecutterMenu.class
})
public class ScreenHandlerMixin {
	@Inject(method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("HEAD"), cancellable = true)
	public void noCameraInteract(Player player, CallbackInfoReturnable<Boolean> cir) {
		if (CameraViewer.inCamera(player.getUUID()))
			cir.setReturnValue(false);
	}
}
