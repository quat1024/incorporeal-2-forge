package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.block.tile.RhodoFunnelTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import vazkii.botania.common.core.helper.Vector3;

public class FunnelTileRenderer extends ComputerTileRenderer<RhodoFunnelTile> {
	public FunnelTileRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(RhodoFunnelTile tile, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int light, int overlay) {
		if(tile.getWorld() == null) return;
		
		Vector3 foreBinding = tile.getForeDirectBind();
		Vector3 aftBinding = tile.getAftDirectBind();
		
		int color = (foreBinding != null && aftBinding != null) ? 0xFF22EE44 : 0xFF119944;
		
		if(foreBinding != null) {
			renderBinding(ms, buf,
				Vector3.fromTileEntityCenter(tile),
				foreBinding,
				color,
				MathHelper.hash(tile.getPos().hashCode()),
				1.3f,
				0.1f,
				2f
			);
		}
		
		if(aftBinding != null) {
			renderBinding(ms, buf,
				Vector3.fromTileEntityCenter(tile),
				aftBinding,
				color,
				MathHelper.hash(tile.getPos().hashCode()),
				0.1f,
				1.3f,
				-2f
			);
		}
	}
}
