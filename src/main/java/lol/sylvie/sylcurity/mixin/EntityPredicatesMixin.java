package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public class EntityPredicatesMixin {
	@Shadow
	public static final Predicate<Entity> NO_SPECTATORS = (entity) -> !entity.isSpectator() && !CameraViewer.inCamera(entity.getUUID());
}
