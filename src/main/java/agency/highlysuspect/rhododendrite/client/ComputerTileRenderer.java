package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.rhododendrite.block.tile.RhodoCellTile;
import agency.highlysuspect.rhododendrite.block.tile.RhodoFunnelTile;
import agency.highlysuspect.rhododendrite.block.tile.RhodoOpTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.core.helper.Vector3;

public abstract class ComputerTileRenderer<T extends TileEntity> extends TileEntityRenderer<T> {
	public ComputerTileRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	protected void renderBinding(MatrixStack ms, IRenderTypeBuffer buffers, Vector3 start, Vector3 end, int color, int hash, float sizeStart, float sizeEnd, float twistiness) {
		Vector3 path = end.subtract(start);
		float pathMag = (float) path.mag();
		if(pathMag < 0.01) return;
		
		//Ok so uh, here I'm making a coordinate frame, where one of the axes (arbitrarily labeled "y") is along the path vector.
		
		//Get the first axis. I need a vector perpendicular to the path vector.
		//First, I choose an arbitrary vector that is not colinear to the path vector.
		//(Here I pick the up vector, unless the path already points sorta up/down.)
		//Then, I take the cross product with the path vector, giving me a vector perpendicular to both.
		Vector3 frameX = path.crossProduct((Math.abs(path.x) < 0.01 && Math.abs(path.z) < 0.01) ? new Vector3(1, 0, 0) : new Vector3(0, 1, 0)).normalize();
		//And get the second axis by using the cross product again to find a vector perpendicular to both.
		Vector3 frameZ = path.crossProduct(frameX).normalize();
		
		//The distance to step along the path vector for each vertex, or, the distance between each vertex.
		Vector3 step = path.normalize().multiply(0.03);
		float stepMag = (float) step.mag();
		//How many steps I will take along the path in total.
		int stepCount = (int) (path.mag() / step.mag());
		if(stepCount == 0) return;
		
		float[] positions = new float[stepCount * 3];
		float magic = -ClientTickHandler.total / 100F + (hash % 10000);
		for(int i = 0; i < stepCount; i++) {
			//How far along the path vector this vertex is, as a percentage.
			float lerp = i / (float) stepCount;
			
			//The current size of the beam.
			float size = Inc.rangeRemap(lerp, 0, 1, sizeStart, sizeEnd);
			//Perturb that size a bit with this magic formula!!! Idk its copied from botania lol
			magic += stepMag * twistiness;
			float ampl = (0.15f * (MathHelper.sin(magic * 2f) * 0.5f + 0.5f) + 0.1f) * size;
			//The X and Z components of the spiral.
			//Original botania added randomness here, but i want a smoother look
			float xComponent = MathHelper.sin(magic * 20) * ampl;
			float zComponent = MathHelper.cos(magic * 20) * ampl;
			
			//Urrrrgh this allocates a ton im sorry. a mutable Vector3 class would come in handy here.
			Vector3 finalPos = step.multiply(i).add(frameX.multiply(xComponent)).add(frameZ.multiply(zComponent));
			positions[i * 3]     = (float) finalPos.x;
			positions[i * 3 + 1] = (float) finalPos.y;
			positions[i * 3 + 2] = (float) finalPos.z;
		}
		
		//Submit everything to the IVertexBuilder
		ms.push();
		ms.translate(0.5, 0.5, 0.5); //TODO why do I need the .5 offset lol?? I already added that in the vector3d
		
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		
		//Todo, can i use a LINE_STRIP.
		IVertexBuilder buffer = buffers.getBuffer(RenderHelper.LINE_1);
		for(int i = 3; i < positions.length; i += 3) {
			buffer.pos(ms.getLast().getMatrix(), positions[i - 3], positions[i - 2], positions[i - 1]).color(r, g, b, a).endVertex();
			buffer.pos(ms.getLast().getMatrix(), positions[i], positions[i + 1], positions[i + 2]).color(r, g, b, a).endVertex();
		}
		
		ms.pop();
	}
	
	public static class Cell extends ComputerTileRenderer<RhodoCellTile> {
		public Cell(TileEntityRendererDispatcher dispatcher) {
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
		}
	}
	
	public static class Op extends ComputerTileRenderer<RhodoOpTile> {
		public Op(TileEntityRendererDispatcher dispatcher) {
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
		}
	}
	
	public static class Funnel extends ComputerTileRenderer<RhodoFunnelTile> {
		public Funnel(TileEntityRendererDispatcher dispatcher) {
			super(dispatcher);
		}
		
		@Override
		public void render(RhodoFunnelTile tile, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int light, int overlay) {
			if(tile.getWorld() == null) return;
			
			Vector3 foreBinding = tile.getForeDirectBind();
			if(foreBinding != null) {
				renderBinding(ms, buf,
					Vector3.fromTileEntityCenter(tile),
					foreBinding,
					0xFF22CC44,
					MathHelper.hash(tile.getPos().hashCode()),
					1.3f,
					0.1f,
					2f
				);
			}
			
			Vector3 aftBinding = tile.getAftDirectBind();
			if(aftBinding != null) {
				renderBinding(ms, buf,
					Vector3.fromTileEntityCenter(tile),
					aftBinding,
					0xFF22CC44,
					MathHelper.hash(tile.getPos().hashCode()),
					0.1f,
					1.3f,
					-2f
				);
			}
		}
	}
}
