package lol.sylvie.sylcurity.mixin;

import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class EntityPredicatesMixin {
	@Shadow
	public static final Predicate<Entity> EXCEPT_SPECTATOR = (entity) -> !entity.isSpectator() && !CameraViewer.inCamera(entity.getUuid());
}
