package agency.highlysuspect.rhododendrite;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("CanBeFinal")
public class WoodFamily {
	public WoodFamily(String name, MaterialColor planksColor, MaterialColor barkColor) {
		this.name = name;
		this.woodType = WoodType.register(WoodType.create(name));
		
		planks = new Block(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2, 3)
			.sound(SoundType.WOOD));
		
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
				if(toolType == ToolType.AXE)
					return strippedLog.getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS));
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
				if(toolType == ToolType.AXE)
					return strippedWood.getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS));
				return super.getToolModifiedState(state, world, pos, player, stack, toolType);
			}
		};
		
		leaves = new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).
			hardnessAndResistance(0.2F)
			.tickRandomly()
			.sound(SoundType.PLANT)
			.notSolid()
			.setAllowsSpawn((state, reader, pos, entity) -> entity == EntityType.OCELOT || entity == EntityType.PARROT) //inlined allowsSpawnOnLeaves
			.setSuffocates((state, reader, pos) -> false) //inlined isntSolid
			.setBlocksVision((state, reader, pos) -> false));
		
		//supplier is a forge extension
		stairs = new StairsBlock(planks::getDefaultState, AbstractBlock.Properties.from(planks));

//		sign = new StandingSignBlock(AbstractBlock.Properties.create(Material.WOOD)
//			.doesNotBlockMovement()
//			.hardnessAndResistance(1f)
//			.sound(SoundType.WOOD),
//			woodType);

//		door = new DoorBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
//			.hardnessAndResistance(3)
//			.sound(SoundType.WOOD)
//			.notSolid());

//		wallSign = new WallSignBlock(AbstractBlock.Properties.create(Material.WOOD)
//			.doesNotBlockMovement()
//			.hardnessAndResistance(1f)
//			.sound(SoundType.WOOD)
//			.lootFrom(() -> sign),
//			woodType);
		
		pressurePlate = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.doesNotBlockMovement()
			.hardnessAndResistance(.5f)
			.sound(SoundType.WOOD));
		
		fence = new FenceBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));

//		trapdoor = new TrapDoorBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
//			.hardnessAndResistance(3f)
//			.sound(SoundType.WOOD)
//			.notSolid()
//			.setAllowsSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false));
		
		fenceGate = new FenceGateBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));
		
		button = new WoodButtonBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS)
			.doesNotBlockMovement()
			.hardnessAndResistance(0.5f)
			.sound(SoundType.WOOD));
		
		slab = new SlabBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));
		
		//TODO enum hell (need a custom entity, ugh)
		//boat = new BoatItem(BoatEntity.Type.DARK_OAK, RhoItems.defaultProps().maxStackSize(1));
		
		//this is literally just a straight-up copy of the birch tree with some numbers fudged a bit
		//yeah idk how this works
		//i tried making it bigger so it was slightly creative but it just looked ugly
		treeFeature = Feature.TREE.withConfiguration(new BaseTreeFeatureConfig.Builder(
			new SimpleBlockStateProvider(log.getDefaultState()),
			new SimpleBlockStateProvider(leaves.getDefaultState()),
			new BlobFoliagePlacer(
				FeatureSpread.func_242252_a(2),
				FeatureSpread.func_242252_a(0),
				3
			),
			new StraightTrunkPlacer(6, 3, 0),
			new TwoLayerFeature(1, 0, 1)
		).setIgnoreVines().build());
		
		tree = new Tree() {
			@Nullable
			@Override
			protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean largeHive) {
				return WoodFamily.this.treeFeature;
			}
		};
		
		sapling = new SaplingBlock(tree, AbstractBlock.Properties.from(Blocks.OAK_SAPLING));
		pottedSapling = new FlowerPotBlock(sapling, AbstractBlock.Properties.from(Blocks.POTTED_OAK_SAPLING));
	}
	
	public String name;
	public WoodType woodType;
	
	public Block planks;
	public RotatedPillarBlock log;
	public RotatedPillarBlock strippedLog;
	public RotatedPillarBlock wood;
	public RotatedPillarBlock strippedWood;
	public Block leaves;
	public StairsBlock stairs;
	//public StandingSignBlock sign;
	//public DoorBlock door;
	//public WallSignBlock wallSign;
	public PressurePlateBlock pressurePlate;
	public FenceBlock fence;
	//public TrapDoorBlock trapdoor;
	public FenceGateBlock fenceGate;
	public WoodButtonBlock button;
	public SlabBlock slab;
	
	//public BoatItem boat;
	
	public Block sapling;
	public Block pottedSapling;
	
	//tree hell
	public Tree tree;
	public ConfiguredFeature<BaseTreeFeatureConfig, ?> treeFeature;
	
	public void registerBlocks(IForgeRegistry<Block> r) {
		Rho.reg(r, name + "_planks", planks);
		Rho.reg(r, name + "_log", log);
		Rho.reg(r, "stripped_" + name + "_log", strippedLog);
		Rho.reg(r, name + "_wood", wood);
		Rho.reg(r, "stripped_" + name + "_wood", strippedWood);
		Rho.reg(r, name + "_leaves", leaves);
		Rho.reg(r, name + "_stairs", stairs);
		//Rho.reg(r, name + "_sign", sign);
		//Rho.reg(r, name + "_door", door);
		//Rho.reg(r, name + "_wall_sign", wallSign);
		Rho.reg(r, name + "_pressure_plate", pressurePlate);
		Rho.reg(r, name + "_fence", fence);
		//Rho.reg(r, name + "_trapdoor", trapdoor);
		Rho.reg(r, name + "_fence_gate", fenceGate);
		Rho.reg(r, name + "_button", button);
		Rho.reg(r, name + "_slab", slab);
		Rho.reg(r, name + "_sapling", sapling);
		Rho.reg(r, "potted_" + name + "_sapling", pottedSapling);
	}
	
	public void registerItems(IForgeRegistry<Item> r) {
		Rho.simpleBlockItems(r,
			planks,
			log,
			strippedLog,
			wood,
			strippedWood,
			leaves,
			stairs,
			//door,
			pressurePlate,
			fence,
			//trapdoor,
			fenceGate,
			button,
			slab,
			sapling
		);
		
		//Rho.reg(r, name + "_sign", new SignItem(RhoItems.defaultProps(), sign, wallSign));
		//Rho.reg(r, name + "_boat", boat);
	}
	
	public void registerFeature(IForgeRegistry<Feature<?>> r) {
		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, Rho.id(name + "_tree_feature"), treeFeature);
	}
}
