package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.IncTags;
import agency.highlysuspect.incorporeal.entity.FracturedSpaceCollectorEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.wand.ICoordBoundItem;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileOpenCrate;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityDoppleganger;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class FracturedSpaceRodItem extends Item implements IManaUsingItem, ICoordBoundItem {
	public FracturedSpaceRodItem(Properties properties) {
		super(properties);
	}
	
	public static final String CRATE_POS_KEY = "CratePos";
	public static final String CRATE_DIMENSION_KEY = "CrateDimension";
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if(!EntityDoppleganger.isTruePlayer(player)) return InteractionResult.FAIL;
		
		Level world = context.getLevel();
		ItemStack held = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		
		BlockState hitState = world.getBlockState(pos);
		BlockEntity hitTile = world.getBlockEntity(pos);
		
		if(hitState.is(IncTags.Blocks.OPEN_CRATES) && hitTile instanceof TileOpenCrate) {
			//Clicked a crate. Remember this position.
			ItemNBTHelper.setCompound(held, CRATE_POS_KEY, NbtUtils.writeBlockPos(pos));
			ItemNBTHelper.setString(held, CRATE_DIMENSION_KEY, world.dimension().location().toString());
			
			player.displayClientMessage(new TranslatableComponent("incorporeal.fractured_space.saved").withStyle(ChatFormatting.DARK_PURPLE), true);
		} else {
			//you didnt click a crate. Spawn an entity.
			if(context.getClickedFace() != Direction.UP) return InteractionResult.PASS; //Click the top of the block
			
			CompoundTag cratePosNbt = ItemNBTHelper.getCompound(held, CRATE_POS_KEY, true);
			String crateDimensionStr = ItemNBTHelper.getString(held, CRATE_DIMENSION_KEY, "###");
			if(cratePosNbt == null || crateDimensionStr.equals("###")) {
				player.displayClientMessage(new TranslatableComponent("incorporeal.fractured_space.no_pos").withStyle(ChatFormatting.RED), true);
				return InteractionResult.FAIL;
			}
			
			if(!world.dimension().location().toString().equals(crateDimensionStr)) {
				player.displayClientMessage(new TranslatableComponent("incorporeal.fractured_space.wrong_dimension").withStyle(ChatFormatting.RED), true);
				return InteractionResult.FAIL;
			}
			
			if(!world.isClientSide) {
				//One final server-only sanity check, since this loads the chunk
				BlockPos cratePos = NbtUtils.readBlockPos(cratePosNbt);
				BlockState rememberedState = world.getBlockState(cratePos);
				BlockEntity rememberedTile = world.getBlockEntity(cratePos);
				
				if(!(rememberedState.is(IncTags.Blocks.OPEN_CRATES) && rememberedTile instanceof TileOpenCrate)) {
					player.displayClientMessage(new TranslatableComponent("incorporeal.fractured_space.no_crate_there").withStyle(ChatFormatting.RED), true);
					return InteractionResult.FAIL;
				}
				
				//Spawn the entity.
				FracturedSpaceCollectorEntity fsc = new FracturedSpaceCollectorEntity(world, cratePos, player);
				fsc.setPos(context.getClickLocation().x, pos.getY() + 1, context.getClickLocation().z);
				world.addFreshEntity(fsc);
			}
		}
		
		return InteractionResult.SUCCESS;
	}
	
	@Nullable
	@Override
	public BlockPos getBinding(Level world, ItemStack stack) {
		CompoundTag cratePosCmp = ItemNBTHelper.getCompound(stack, CRATE_POS_KEY, true);
		if(cratePosCmp == null) return null;
		
		String dimensionStr = ItemNBTHelper.getString(stack, CRATE_DIMENSION_KEY, "###");
		if(!world.dimension().location().toString().equals(dimensionStr)) return null;
		
		else return NbtUtils.readBlockPos(cratePosCmp);
	}
	
	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag mistake) {
		if(world == null) return;
		
		CompoundTag cratePosCmp = ItemNBTHelper.getCompound(stack, CRATE_POS_KEY, true);
		if(cratePosCmp == null) {
			tooltip.add(new TranslatableComponent("incorporeal.fractured_space.tooltip.not_bound").withStyle(ChatFormatting.RED));
		} else {
			tooltip.add(new TranslatableComponent("incorporeal.fractured_space.tooltip.bound").withStyle(ChatFormatting.GREEN));
			
			String dimensionStr = ItemNBTHelper.getString(stack, CRATE_DIMENSION_KEY, "###");
			if(!world.dimension().location().toString().equals(dimensionStr)) {
				tooltip.add(new TranslatableComponent("incorporeal.fractured_space.tooltip.wrong_dimension").withStyle(ChatFormatting.RED));
			}
			
			if(mistake.isAdvanced()) {
				BlockPos pos = NbtUtils.readBlockPos(cratePosCmp);
				tooltip.add(new TranslatableComponent("incorporeal.fractured_space.tooltip.debug.pos", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
				tooltip.add(new TranslatableComponent("incorporeal.fractured_space.tooltip.debug.dim", dimensionStr).withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
