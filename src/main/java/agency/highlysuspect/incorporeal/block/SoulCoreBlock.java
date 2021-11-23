package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.AbstractSoulCoreTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.entity.EntityDoppleganger;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SoulCoreBlock extends Block implements IWandHUD {
	public SoulCoreBlock(Properties properties, Supplier<BlockEntityType<? extends AbstractSoulCoreTile>> typeS) {
		super(properties);
		this.typeS = typeS;
	}
	
	//Using a supplier here since tiletypes depend on blocks and this block depends on the tile type.
	private final Supplier<BlockEntityType<? extends AbstractSoulCoreTile>> typeS;
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return typeS.get().create();
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(!EntityDoppleganger.isTruePlayer(player)) return InteractionResult.PASS;
		
		AbstractSoulCoreTile tile = typeS.get().getBlockEntity(world, pos);
		if(tile != null) return tile.activate(player, hand);
		else return InteractionResult.PASS;
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		AbstractSoulCoreTile tile = typeS.get().getBlockEntity(world, pos);
		if(tile != null) return tile.getComparator();
		else return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHUD(PoseStack ms, Minecraft mc, Level world, BlockPos pos) {
		AbstractSoulCoreTile tile = typeS.get().getBlockEntity(world, pos);
		if(tile != null) tile.renderHUD(ms, mc, world, pos);
		else HUDHandler.drawSimpleManaHUD(ms, 0xff0000, 1, 1, "Missing tile entity?");
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
}
