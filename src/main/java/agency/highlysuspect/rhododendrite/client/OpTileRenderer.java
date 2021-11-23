package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.block.tile.RhodoOpTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.core.helper.Vector3;

public class OpTileRenderer extends ComputerTileRenderer<RhodoOpTile> {
	public OpTileRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(RhodoOpTile tile, float partialTicks, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
		if(tile.getLevel() == null) return;
		
		BlockPos directBindPos = tile.getDirectBind();
		if(directBindPos != null) {
			renderBinding(ms, buf,
				Vector3.fromTileEntityCenter(tile),
				Vector3.fromBlockPos(directBindPos).add(.5, .5, .5),
				0xFF2277BB,
				Mth.murmurHash3Mixer(tile.getBlockPos().hashCode()),
				1f,
				0.5f,
				1f
			);
		}
		
		ItemStack card = tile.getCard();
		if(!card.isEmpty()) {
			ms.pushPose();
			ms.translate(0.5, 0.5, 0.5);
			ms.scale(0.6f, 0.6f, 0.6f);
			//ms.rotate(Vector3f.YP.rotationDegrees(ClientTickHandler.total % 360));
			ms.mulPose(Vector3f.YP.rotationDegrees(-Minecraft.getInstance().player.yRot)); //we have billboarding at home
			ms.translate(0, Mth.sin(ClientTickHandler.total / 10f) * 0.1f, 0);
			Minecraft.getInstance().getItemRenderer().renderStatic(card, ItemTransforms.TransformType.FIXED, light, overlay, ms, buf);
			ms.popPose();
		}
	}
}
