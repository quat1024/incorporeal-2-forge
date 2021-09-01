package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.block.tile.AwakenedLogTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class AwakenedLogRenderer extends TileEntityRenderer<AwakenedLogTile> {
	public AwakenedLogRenderer(TileEntityRendererDispatcher gaming) {
		super(gaming);
	}
	
	@Override
	public void render(AwakenedLogTile tile, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int light, int overlay) {
		
	}
}
