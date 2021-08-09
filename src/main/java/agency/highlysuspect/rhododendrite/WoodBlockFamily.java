package agency.highlysuspect.rhododendrite;

import agency.highlysuspect.rhododendrite.item.RhoItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

public class WoodBlockFamily {
	public WoodBlockFamily(String name, MaterialColor planksColor, MaterialColor barkColor) {
		this.name = name;
		this.woodType = WoodType.register(WoodType.create(name));
		
		planks = new Block(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2, 3)
			.sound(SoundType.WOOD));
		
		//sapling = null; //TODO (needs Tree)
		
		strippedLog = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2.0F)
			.sound(SoundType.WOOD));
		
		log = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, state -> state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? planksColor : barkColor)
			.hardnessAndResistance(2.0F)
			.sound(SoundType.WOOD)) {
			//In lieu of letting you just add things to the stinkin axe-item stripping map, Forge provides... whatever this is?
			//Yeah man I dunno.
			@Nullable
			@Override
			public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
				if(toolType == ToolType.AXE) return strippedLog.getDefaultState().with(BlockStateProperties.AXIS, state.get(BlockStateProperties.AXIS));
				return super.getToolModifiedState(state, world, pos, player, stack, toolType);
			}
		};
		
		strippedWood = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2)
			.sound(SoundType.WOOD));
		
		wood = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, barkColor)
			.hardnessAndResistance(2)
			.sound(SoundType.WOOD)) {
			@Nullable
			@Override
			public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
				if(toolType == ToolType.AXE) return strippedWood.getDefaultState().with(BlockStateProperties.AXIS, state.get(BlockStateProperties.AXIS));
				return super.getToolModifiedState(state, world, pos, player, stack, toolType);
			}
		};
		
		leaves = new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).
			hardnessAndResistance(0.2F)
			.tickRandomly()
			.sound(SoundType.PLANT)
			.notSolid()
			.setAllowsSpawn((state, reader, pos, entity) -> entity == EntityType.OCELOT || entity == EntityType.PARROT) //inlined allowsSpawnOnLeaves
			.setSuffocates((state1, reader1, pos1) -> false) //inlined isntSolid
			.setBlocksVision((state2, reader2, pos2) -> false));
		
		//supplier is a forge extension
		stairs = new StairsBlock(planks::getDefaultState, AbstractBlock.Properties.from(planks));
		
		sign = new StandingSignBlock(AbstractBlock.Properties.create(Material.WOOD)
			.doesNotBlockMovement()
			.hardnessAndResistance(1f)
			.sound(SoundType.WOOD),
			woodType);
		
		door = new DoorBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(3)
			.sound(SoundType.WOOD)
			.notSolid());
		
		wallSign = new WallSignBlock(AbstractBlock.Properties.create(Material.WOOD)
			.doesNotBlockMovement()
			.hardnessAndResistance(1f)
			.sound(SoundType.WOOD)
			.lootFrom(() -> sign),
			woodType);
		
		pressurePlate = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.doesNotBlockMovement()
			.hardnessAndResistance(.5f)
			.sound(SoundType.WOOD));
		
		fence = new FenceBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));
		
		trapdoor = new TrapDoorBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(3f)
			.sound(SoundType.WOOD)
			.notSolid()
			.setAllowsSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false));
		
		fenceGate = new FenceGateBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));
		
//		pottedSapling = new FlowerPotBlock(sapling, AbstractBlock.Properties.create(Material.MISCELLANEOUS)
//			.zeroHardnessAndResistance()
//			.notSolid()); //TODO (needs sapling)
		
		button = new WoodButtonBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS)
			.doesNotBlockMovement()
			.hardnessAndResistance(0.5f)
			.sound(SoundType.WOOD));
		
		slab = new SlabBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));
	}
	
	public final String name;
	public final WoodType woodType;
	
	public final Block planks;
	//public final Block sapling;
	public final RotatedPillarBlock log;
	public final RotatedPillarBlock strippedLog;
	public final RotatedPillarBlock wood;
	public final RotatedPillarBlock strippedWood;
	public final Block leaves;
	public final StairsBlock stairs;
	public final StandingSignBlock sign;
	public final DoorBlock door;
	public final WallSignBlock wallSign;
	public final PressurePlateBlock pressurePlate;
	public final FenceBlock fence;
	public final TrapDoorBlock trapdoor;
	public final FenceGateBlock fenceGate;
	//public final Block pottedSapling;
	public final WoodButtonBlock button;
	public final SlabBlock slab;
	
	public void registerBlocks(IForgeRegistry<Block> r) {
		Rho.reg(r, name + "_planks", planks);
		//sapling
		Rho.reg(r, name + "_log", log);
		Rho.reg(r, "stripped_" + name + "_log", strippedLog);
		Rho.reg(r, name + "_wood", wood);
		Rho.reg(r, "stripped_" + name + "_wood", strippedWood);
		Rho.reg(r, name + "_leaves", leaves);
		Rho.reg(r, name + "_stairs", stairs);
		Rho.reg(r, name + "_sign", sign);
		Rho.reg(r, name + "_door", door);
		Rho.reg(r, name + "_wall_sign", wallSign);
		Rho.reg(r, name + "_pressure_plate", pressurePlate);
		Rho.reg(r, name + "_fence", fence);
		Rho.reg(r, name + "_trapdoor", trapdoor);
		Rho.reg(r, name + "_fence_gate", fenceGate);
		//potted sapling
		Rho.reg(r, name + "_button", button);
		Rho.reg(r, name + "_slab", slab);
	}
	
	public void registerItems(IForgeRegistry<Item> r) {
		Rho.simpleBlockItems(r, planks, log, strippedLog, wood, strippedWood, leaves, stairs, door, pressurePlate, fence, trapdoor, fenceGate, button, slab);
		//need to add sapling though
		
		Rho.reg(r, name + "_sign", new SignItem(RhoItems.defaultProps(), sign, wallSign));
	}
}
