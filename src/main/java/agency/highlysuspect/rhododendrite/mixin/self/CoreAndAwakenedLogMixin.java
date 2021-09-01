package agency.highlysuspect.rhododendrite.mixin.self;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.AwakenedLogBlock;
import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.tile.RequestHolderTile;
import agency.highlysuspect.rhododendrite.client.MatcherRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.api.wand.IWandHUD;

//these are pretty much the same thing so, it's here now instead of copy-pasting the code across the two classes
@Mixin({
	CoreBlock.class,
	AwakenedLogBlock.class
})
public class CoreAndAwakenedLogMixin extends Block implements IWandHUD {
	public CoreAndAwakenedLogMixin(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RequestHolderTile) return ((RequestHolderTile) tile).signalStrength();
		else return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHUD(MatrixStack ms, Minecraft mc, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RequestHolderTile) {
			SolidifiedRequest request = ((RequestHolderTile) tile).getRequest();
			MatcherRenderer.INSTANCE.drawInGui(request, mc, ms, world);
		}
	}
}
