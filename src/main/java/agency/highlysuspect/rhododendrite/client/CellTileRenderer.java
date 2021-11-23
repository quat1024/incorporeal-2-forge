package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.tile.RhodoCellTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import vazkii.botania.common.core.helper.Vector3;

public class CellTileRenderer extends ComputerTileRenderer<RhodoCellTile> {
	public CellTileRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(RhodoCellTile tile, float partialTicks, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
		if(tile.getLevel() == null) return;
		
		BlockPos directBindPos = tile.getBind();
		if(directBindPos != null) {
			renderBinding(ms, buf,
				Vector3.fromTileEntityCenter(tile),
				Vector3.fromBlockPos(directBindPos).add(.5, .5, .5),
				0xFFFF5511,
				Mth.murmurHash3Mixer(tile.getBlockPos().hashCode()),
				1.7f,
				0.1f,
				0.5f
			);
		}
		
		SolidifiedRequest contents = tile.peek();
		
		//I KNOW THIS LOOKS LIKE CRAP LOL
		Font font = renderer.getFont();
		ms.pushPose();
		ms.translate(0.5f, 0.5f, 0.5f);
		ms.mulPose(Vector3f.YP.rotationDegrees(-Minecraft.getInstance().player.yRot + 180)); //we have billboarding at home
		ms.scale(1/64f, -1/64f, 1/64f); //troled
		String pog = contents.toText().getString();
		font.drawShadow(ms, pog, -font.width(pog) / 2f, 0, 0xFF0000);
		ms.popPose();
	}
}
