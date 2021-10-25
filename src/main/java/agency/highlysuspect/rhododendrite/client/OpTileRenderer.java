package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.block.tile.RhodoOpTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.core.helper.Vector3;

public class OpTileRenderer extends ComputerTileRenderer<RhodoOpTile> {
	public OpTileRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(RhodoOpTile tile, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int light, int overlay) {
		if(tile.getWorld() == null) return;
		
		BlockPos directBindPos = tile.getDirectBind();
		if(directBindPos != null) {
			renderBinding(ms, buf,
				Vector3.fromTileEntityCenter(tile),
				Vector3.fromBlockPos(directBindPos).add(.5, .5, .5),
				0xFF2277BB,
				MathHelper.hash(tile.getPos().hashCode()),
				1f,
				0.5f,
				1f
			);
		}
		
		ItemStack card = tile.getCard();
		if(!card.isEmpty()) {
			ms.push();
			ms.translate(0.5, 0.5, 0.5);
			ms.scale(0.6f, 0.6f, 0.6f);
			//ms.rotate(Vector3f.YP.rotationDegrees(ClientTickHandler.total % 360));
			ms.rotate(Vector3f.YP.rotationDegrees(-Minecraft.getInstance().player.rotationYaw)); //we have billboarding at home
			ms.translate(0, MathHelper.sin(ClientTickHandler.total / 10f) * 0.1f, 0);
			Minecraft.getInstance().getItemRenderer().renderItem(card, ItemCameraTransforms.TransformType.FIXED, light, overlay, ms, buf);
			ms.pop();
		}
	}
}
