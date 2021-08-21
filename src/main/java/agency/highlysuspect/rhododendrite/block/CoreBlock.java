package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.block.tile.RequestHolderTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.api.wand.IWandHUD;

import javax.annotation.Nullable;

public class CoreBlock extends DirectionalBlock implements IWandHUD {
	public CoreBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(FACING, Direction.UP));
	}
	
	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(FACING));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getFace().getOpposite());
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CoreTile();
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
			//TODO this looks like shit, at least it's something, and i copypasted it from AwakenedLogBlock
			
			SolidifiedRequest request = ((RequestHolderTile) tile).getRequest();
			
			//Copy paste from corporea retainer
			String yeet = request.toText().getString();
			int x = mc.getMainWindow().getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(yeet) / 2;
			int y = mc.getMainWindow().getScaledHeight() / 2 + 10;
			
			//noinspection ConstantConditions
			mc.fontRenderer.drawStringWithShadow(ms, yeet, x, y, TextFormatting.GREEN.getColor());
		}
	}
}
