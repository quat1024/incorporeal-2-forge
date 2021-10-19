package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.Inc;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.core.helper.Vector3;

public abstract class ComputerTileRenderer<T extends TileEntity> extends TileEntityRenderer<T> {
	public ComputerTileRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	protected void renderBinding(MatrixStack ms, IRenderTypeBuffer buffers, Vector3 start, Vector3 end, int color, int hash, float sizeStart, float sizeEnd, float twistiness) {
		//HEY Maybe don't copy this... Lol
		//The intention was to draw vertices in world space (?) but it doesn't actually work.
		//For one, the "start" vec3 is kind of ignored.
		//It's only used to calculate the length/direction of the path, and the spiral always stems from 0, 0, 0.
		//Found that out when i tried to make the spirals stem from the front side of the block instead of the center.
		//One day when I'm less burnt out on rendering this would be worth taking another look at.
		
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
			positions[i * 3] = (float) finalPos.x;
			positions[i * 3 + 1] = (float) finalPos.y;
			positions[i * 3 + 2] = (float) finalPos.z;
		}
		
		//Submit everything to the IVertexBuilder
		ms.push();
		ms.translate(0.5, 0.5, 0.5); //lame hack because it's not actually in world space
		
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		
		IVertexBuilder buffer = buffers.getBuffer(RenderHelper.LINE_1);
		for(int i = 3; i < positions.length; i += 3) {
			buffer.pos(ms.getLast().getMatrix(), positions[i - 3], positions[i - 2], positions[i - 1]).color(r, g, b, a).endVertex();
			buffer.pos(ms.getLast().getMatrix(), positions[i], positions[i + 1], positions[i + 2]).color(r, g, b, a).endVertex();
		}
		
		ms.pop();
	}
}