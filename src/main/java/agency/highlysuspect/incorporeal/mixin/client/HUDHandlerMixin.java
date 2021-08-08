package agency.highlysuspect.incorporeal.mixin.client;

import agency.highlysuspect.incorporeal.client.IncHudHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.core.handler.HUDHandler;

@Mixin(HUDHandler.class)
public class HUDHandlerMixin {
	@Inject(
		method = "onDrawScreenPost",
		at = @At("HEAD"),
		remap = false
	)
	private static void start(RenderGameOverlayEvent.Post event, CallbackInfo ci) {
		//reset it every frame
		IncHudHandler.botaniaDrewCorporeaIndexOverlay = false;
	}
	
	@Inject(
		method = "renderNearIndexDisplay",
		at = @At("HEAD"),
		remap = false
	)
	private static void whenRenderingNearIndexDisplay(MatrixStack ms, CallbackInfo ci) {
		IncHudHandler.botaniaDrewCorporeaIndexOverlay = true;
	}
}
