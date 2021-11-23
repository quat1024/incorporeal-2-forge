package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.IncTags;
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

import net.minecraft.item.Item.Properties;

public class FracturedSpaceRodItem extends Item implements IManaUsingItem, ICoordBoundItem {
	public FracturedSpaceRodItem(Properties properties) {
		super(properties);
	}
	
	public static final String CRATE_POS_KEY = "CratePos";
	public static final String CRATE_DIMENSION_KEY = "CrateDimension";
	
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if(!EntityDoppleganger.isTruePlayer(player)) return ActionResultType.FAIL;
		
		World world = context.getLevel();
		ItemStack held = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		
		BlockState hitState = world.getBlockState(pos);
		TileEntity hitTile = world.getBlockEntity(pos);
		
		if(hitState.is(IncTags.Blocks.OPEN_CRATES) && hitTile instanceof TileOpenCrate) {
			//Clicked a crate. Remember this position.
			ItemNBTHelper.setCompound(held, CRATE_POS_KEY, NBTUtil.writeBlockPos(pos));
			ItemNBTHelper.setString(held, CRATE_DIMENSION_KEY, world.dimension().location().toString());
			
			player.displayClientMessage(new TranslationTextComponent("incorporeal.fractured_space.saved").withStyle(TextFormatting.DARK_PURPLE), true);
		} else {
			//you didnt click a crate. Spawn an entity.
			if(context.getClickedFace() != Direction.UP) return ActionResultType.PASS; //Click the top of the block
			
			CompoundNBT cratePosNbt = ItemNBTHelper.getCompound(held, CRATE_POS_KEY, true);
			String crateDimensionStr = ItemNBTHelper.getString(held, CRATE_DIMENSION_KEY, "###");
			if(cratePosNbt == null || crateDimensionStr.equals("###")) {
				player.displayClientMessage(new TranslationTextComponent("incorporeal.fractured_space.no_pos").withStyle(TextFormatting.RED), true);
				return ActionResultType.FAIL;
			}
			
			if(!world.dimension().location().toString().equals(crateDimensionStr)) {
				player.displayClientMessage(new TranslationTextComponent("incorporeal.fractured_space.wrong_dimension").withStyle(TextFormatting.RED), true);
				return ActionResultType.FAIL;
			}
			
			if(!world.isClientSide) {
				//One final server-only sanity check, since this loads the chunk
				BlockPos cratePos = NBTUtil.readBlockPos(cratePosNbt);
				BlockState rememberedState = world.getBlockState(cratePos);
				TileEntity rememberedTile = world.getBlockEntity(cratePos);
				
				if(!(rememberedState.is(IncTags.Blocks.OPEN_CRATES) && rememberedTile instanceof TileOpenCrate)) {
					player.displayClientMessage(new TranslationTextComponent("incorporeal.fractured_space.no_crate_there").withStyle(TextFormatting.RED), true);
					return ActionResultType.FAIL;
				}
				
				//Spawn the entity.
				FracturedSpaceCollectorEntity fsc = new FracturedSpaceCollectorEntity(world, cratePos, player);
				fsc.setPos(context.getClickLocation().x, pos.getY() + 1, context.getClickLocation().z);
				world.addFreshEntity(fsc);
			}
		}
		
		return ActionResultType.SUCCESS;
	}
	
	@Nullable
	@Override
	public BlockPos getBinding(World world, ItemStack stack) {
		CompoundNBT cratePosCmp = ItemNBTHelper.getCompound(stack, CRATE_POS_KEY, true);
		if(cratePosCmp == null) return null;
		
		String dimensionStr = ItemNBTHelper.getString(stack, CRATE_DIMENSION_KEY, "###");
		if(!world.dimension().location().toString().equals(dimensionStr)) return null;
		
		else return NBTUtil.readBlockPos(cratePosCmp);
	}
	
	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag mistake) {
		if(world == null) return;
		
		CompoundNBT cratePosCmp = ItemNBTHelper.getCompound(stack, CRATE_POS_KEY, true);
		if(cratePosCmp == null) {
			tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.not_bound").withStyle(TextFormatting.RED));
		} else {
			tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.bound").withStyle(TextFormatting.GREEN));
			
			String dimensionStr = ItemNBTHelper.getString(stack, CRATE_DIMENSION_KEY, "###");
			if(!world.dimension().location().toString().equals(dimensionStr)) {
				tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.wrong_dimension").withStyle(TextFormatting.RED));
			}
			
			if(mistake.isAdvanced()) {
				BlockPos pos = NBTUtil.readBlockPos(cratePosCmp);
				tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.debug.pos", pos.getX(), pos.getY(), pos.getZ()).withStyle(TextFormatting.GRAY));
				tooltip.add(new TranslationTextComponent("incorporeal.fractured_space.tooltip.debug.dim", dimensionStr).withStyle(TextFormatting.GRAY));
			}
		}
	}
}
