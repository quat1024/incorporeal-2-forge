package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.tile.RhodoCellTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.botania.common.core.helper.Vector3;

public class CellTileRenderer extends ComputerTileRenderer<RhodoCellTile> {
	public CellTileRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(RhodoCellTile tile, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int light, int overlay) {
		if(tile.getWorld() == null) return;
		
		BlockPos directBindPos = tile.getBind();
		if(directBindPos != null) {
			renderBinding(ms, buf,
				Vector3.fromTileEntityCenter(tile),
				Vector3.fromBlockPos(directBindPos).add(.5, .5, .5),
				0xFFFF5511,
				MathHelper.hash(tile.getPos().hashCode()),
				1.7f,
				0.1f,
				0.5f
			);
		}
		
		SolidifiedRequest contents = tile.peek();
		
		//I KNOW THIS LOOKS LIKE CRAP LOL
		FontRenderer font = renderDispatcher.getFontRenderer();
		ms.push();
		ms.translate(0.5f, 0.5f, 0.5f);
		ms.rotate(Vector3f.YP.rotationDegrees(-Minecraft.getInstance().player.rotationYaw + 180)); //we have billboarding at home
		ms.scale(1/64f, -1/64f, 1/64f); //troled
		String pog = contents.toText().getString();
		font.drawStringWithShadow(ms, pog, -font.getStringWidth(pog) / 2f, 0, 0xFF0000);
		ms.pop();
	}
}
