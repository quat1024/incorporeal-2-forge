package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.block.tile.RhodoFunnelTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.util.Mth;
import vazkii.botania.common.core.helper.Vector3;

public class FunnelTileRenderer extends ComputerTileRenderer<RhodoFunnelTile> {
	public FunnelTileRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(RhodoFunnelTile tile, float partialTicks, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
		if(tile.getLevel() == null) return;
		
		Vector3 foreBinding = tile.getForeDirectBind();
		Vector3 aftBinding = tile.getAftDirectBind();
		
		int color = (foreBinding != null && aftBinding != null) ? 0xFF22EE44 : 0xFF119944;
		
		if(foreBinding != null) {
			renderBinding(ms, buf,
				Vector3.fromTileEntityCenter(tile),
				foreBinding,
				color,
				Mth.murmurHash3Mixer(tile.getBlockPos().hashCode()),
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
				Mth.murmurHash3Mixer(tile.getBlockPos().hashCode()),
				0.1f,
				1.3f,
				-2f
			);
		}
	}
}
