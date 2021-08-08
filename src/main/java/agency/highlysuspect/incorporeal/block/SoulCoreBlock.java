package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.AbstractSoulCoreTile;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.entity.EntityDoppleganger;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SoulCoreBlock extends Block implements IWandHUD {
	public SoulCoreBlock(Properties properties, Supplier<TileEntityType<? extends AbstractSoulCoreTile>> typeS) {
		super(properties);
		this.typeS = typeS;
	}
	
	//Using a supplier here since tiletypes depend on blocks and this block depends on the tile type.
	private final Supplier<TileEntityType<? extends AbstractSoulCoreTile>> typeS;
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return typeS.get().create();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(!EntityDoppleganger.isTruePlayer(player)) return ActionResultType.PASS;
		
		AbstractSoulCoreTile tile = typeS.get().getIfExists(world, pos);
		if(tile != null) return tile.activate(player, hand);
		else return ActionResultType.PASS;
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		AbstractSoulCoreTile tile = typeS.get().getIfExists(world, pos);
		if(tile != null) return tile.getComparator();
		else return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHUD(MatrixStack ms, Minecraft mc, World world, BlockPos pos) {
		AbstractSoulCoreTile tile = typeS.get().getIfExists(world, pos);
		if(tile != null) tile.renderHUD(ms, mc, world, pos);
		else HUDHandler.drawSimpleManaHUD(ms, 0xff0000, 1, 1, "Missing tile entity?");
	}
}
