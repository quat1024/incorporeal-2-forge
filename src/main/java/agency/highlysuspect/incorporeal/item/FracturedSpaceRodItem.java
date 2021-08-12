package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.entity.FracturedSpaceCollectorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.wand.ICoordBoundItem;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileOpenCrate;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityDoppleganger;

import javax.annotation.Nullable;
import java.util.List;

public class FracturedSpaceRodItem extends Item implements IManaUsingItem, ICoordBoundItem {
	public FracturedSpaceRodItem(Properties properties) {
		super(properties);
	}
	
	public static final String CRATE_POS_KEY = "CratePos";
	public static final String CRATE_DIMENSION_KEY = "CrateDimension";
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if(!EntityDoppleganger.isTruePlayer(player)) return ActionResultType.FAIL;
		
		World world = context.getWorld();
		ItemStack held = context.getItem();
		BlockPos pos = context.getPos();
		
		BlockState hitState = world.getBlockState(pos);
		TileEntity hitTile = world.getTileEntity(pos);
		
		//TODO tag check (issue #5)
		if(hitTile instanceof TileOpenCrate && hitState.getBlock() == ModBlocks.openCrate) {
			//Clicked a crate. Remember this position.
			ItemNBTHelper.setCompound(held, CRATE_POS_KEY, NBTUtil.writeBlockPos(pos));
			ItemNBTHelper.setString(held, CRATE_DIMENSION_KEY, world.getDimensionKey().getLocation().toString());
			
			player.sendStatusMessage(new TranslationTextComponent("incorporeal.fractured_space.saved").mergeStyle(TextFormatting.DARK_PURPLE), true);
			return ActionResultType.SUCCESS;
		} else {
			//you didnt click a crate. Spawn an entity.
			if(context.getFace() != Direction.UP) return ActionResultType.PASS; //Click the top of the block
			
			CompoundNBT cratePosNbt = ItemNBTHelper.getCompound(held, CRATE_POS_KEY, true);
			String crateDimensionStr = ItemNBTHelper.getString(held, CRATE_DIMENSION_KEY, "###");
			if(cratePosNbt == null || crateDimensionStr.equals("###")) {
				player.sendStatusMessage(new TranslationTextComponent("incorporeal.fractured_space.no_pos").mergeStyle(TextFormatting.RED), true);
				return ActionResultType.FAIL;
			}
			
			if(!world.getDimensionKey().getLocation().toString().equals(crateDimensionStr)) {
				player.sendStatusMessage(new TranslationTextComponent("incorporeal.fractured_space.wrong_dimension").mergeStyle(TextFormatting.RED), true);
				return ActionResultType.FAIL;
			}
			
			if(!world.isRemote) {
				//One final server-only sanity check, since this loads the chunk
				BlockPos cratePos = NBTUtil.readBlockPos(cratePosNbt);
				BlockState rememberedState = world.getBlockState(cratePos);
				TileEntity rememberedTile = world.getTileEntity(cratePos);
				//TODO tag check (issue #5)
				if(!(rememberedTile instanceof TileOpenCrate && rememberedState.getBlock() == ModBlocks.openCrate)) {
					player.sendStatusMessage(new TranslationTextComponent("incorporeal.fractured_space.no_crate_there").mergeStyle(TextFormatting.RED), true);
					return ActionResultType.FAIL;
				}
				
				//Spawn the entity.
				FracturedSpaceCollectorEntity fsc = new FracturedSpaceCollectorEntity(world, cratePos, player);
				fsc.setPosition(context.getHitVec().x, pos.getY() + 1, context.getHitVec().z);
				world.addEntity(fsc);
			}
			
			return ActionResultType.SUCCESS;
		}
	}
	
	@Nullable
	@Override
	public BlockPos getBinding(World world, ItemStack stack) {
		CompoundNBT cratePosCmp = ItemNBTHelper.getCompound(stack, CRATE_POS_KEY, true);
		if(cratePosCmp == null) return null;
		
		String dimensionStr = ItemNBTHelper.getString(stack, CRATE_DIMENSION_KEY, "###");
		if(!world.getDimensionKey().getLocation().toString().equals(dimensionStr)) return null;
		
		else return NBTUtil.readBlockPos(cratePosCmp);
	}
	
	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag mistake) {
		if(world == null) return;
		
		CompoundNBT cratePosCmp = ItemNBTHelper.getCompound(stack, CRATE_POS_KEY, true);
		if(cratePosCmp == null) {
			tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.not_bound").mergeStyle(TextFormatting.RED));
		} else {
			tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.bound").mergeStyle(TextFormatting.GREEN));
			
			String dimensionStr = ItemNBTHelper.getString(stack, CRATE_DIMENSION_KEY, "###");
			if(!world.getDimensionKey().getLocation().toString().equals(dimensionStr)) {
				tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.wrong_dimension").mergeStyle(TextFormatting.RED));
			}
			
			if(mistake.isAdvanced()) {
				BlockPos pos = NBTUtil.readBlockPos(cratePosCmp);
				tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.debug.pos", pos.getX(), pos.getY(), pos.getZ()).mergeStyle(TextFormatting.GRAY));
				tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.debug.dim", dimensionStr).mergeStyle(TextFormatting.GRAY));
			}
		}
	}
}
