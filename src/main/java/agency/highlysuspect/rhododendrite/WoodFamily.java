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
import java.util.function.Consumer;

//Feel free to paste this class into your own mods if it's useful.
//The idea here is: you make a new WoodFamily with some default values, use `with` to set up
//any custom block implementations (if you need to swap any out), then use `defaults` to create
//the rest. It's done that way because some block implementations refer to other block implementations
//like, the tree has to know about the logs, so the logs have to be created before the tree.
//Some of them are commented out; they should work ok, i just don't have textures for them.
//Also see RhoStatesModels for some woodfamily datagen stuff.
public class WoodFamily {
	public WoodFamily(String name, MaterialColor planksColor, MaterialColor barkColor) {
		this.name = name;
		this.woodType = WoodType.register(WoodType.create(name));
		this.planksColor = planksColor;
		this.barkColor = barkColor;
	}
	
	public WoodFamily with(Consumer<WoodFamily> action) {
		action.accept(this);
		return this;
	}
	
	public WoodFamily defaults() {
		if(planks == null) planks = new Block(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2, 3)
			.sound(SoundType.WOOD));
		
		if(strippedLog == null) strippedLog = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2.0F)
			.sound(SoundType.WOOD));
		
		if(log == null) log = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, state -> state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? planksColor : barkColor)
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
		
		if(strippedWood == null) strippedWood = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2)
			.sound(SoundType.WOOD));
		
		if(wood == null) wood = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, barkColor)
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
		
		if(leaves == null) leaves = new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).
			hardnessAndResistance(0.2F)
			.tickRandomly()
			.sound(SoundType.PLANT)
			.notSolid()
			.setAllowsSpawn((state, reader, pos, entity) -> entity == EntityType.OCELOT || entity == EntityType.PARROT) //inlined allowsSpawnOnLeaves
			.setSuffocates((state, reader, pos) -> false) //inlined isntSolid
			.setBlocksVision((state, reader, pos) -> false));
		
		if(stairs == null) stairs = new StairsBlock(planks.getDefaultState(), AbstractBlock.Properties.from(planks));

//		if(sign == null) sign = new StandingSignBlock(AbstractBlock.Properties.create(Material.WOOD)
//			.doesNotBlockMovement()
//			.hardnessAndResistance(1f)
//			.sound(SoundType.WOOD),
//			woodType);

//		if(door == null) door = new DoorBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
//			.hardnessAndResistance(3)
//			.sound(SoundType.WOOD)
//			.notSolid());

//		if(wallSign == null) wallSign = new WallSignBlock(AbstractBlock.Properties.create(Material.WOOD)
//			.doesNotBlockMovement()
//			.hardnessAndResistance(1f)
//			.sound(SoundType.WOOD)
//			.lootFrom(() -> sign),
//			woodType);
		
		if(pressurePlate == null) pressurePlate = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.doesNotBlockMovement()
			.hardnessAndResistance(.5f)
			.sound(SoundType.WOOD));
		
		if(fence == null) fence = new FenceBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));

//		if(trapdoor == null) trapdoor = new TrapDoorBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
//			.hardnessAndResistance(3f)
//			.sound(SoundType.WOOD)
//			.notSolid()
//			.setAllowsSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false));
		
		if(fenceGate == null) fenceGate = new FenceGateBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));
		
		if(button == null) button = new WoodButtonBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS)
			.doesNotBlockMovement()
			.hardnessAndResistance(0.5f)
			.sound(SoundType.WOOD));
		
		if(slab == null) slab = new SlabBlock(AbstractBlock.Properties.create(Material.WOOD, planksColor)
			.hardnessAndResistance(2f, 3f)
			.sound(SoundType.WOOD));
		
		//TODO enum hell (need a custom entity, ugh)
		//boat = new BoatItem(BoatEntity.Type.DARK_OAK, RhoItems.defaultProps().maxStackSize(1));
		
		//this is literally just a straight-up copy of the birch tree with some numbers fudged a bit
		//yeah idk how this tree stuff works
		//i tried making it larger so it was slightly creative, but it looked bad
		if(treeFeature == null) treeFeature = Feature.TREE.withConfiguration(new BaseTreeFeatureConfig.Builder(
			//Trunk blockstate provider
			new SimpleBlockStateProvider(log.getDefaultState()),
			//Leaves blockstate provider
			new SimpleBlockStateProvider(leaves.getDefaultState()),
			//Foliage placer
			new BlobFoliagePlacer(
				FeatureSpread.func_242252_a(2), //method is probably named "exactly" - 0 spread
				FeatureSpread.func_242252_a(0),
				3
			),
			//Trunk placer
			new StraightTrunkPlacer(6, 3, 0),
			//Minimum size (limit, lower, upper)
			new TwoLayerFeature(1, 0, 1)
		).setIgnoreVines().build());
		
		if(tree == null) tree = new Tree() {
			@Nullable
			@Override
			protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random random, boolean nearbyFlower) {
				return WoodFamily.this.treeFeature;
			}
		};
		
		if(sapling == null) sapling = new SaplingBlock(tree, AbstractBlock.Properties.from(Blocks.OAK_SAPLING));
		if(pottedSapling == null) pottedSapling = new FlowerPotBlock(sapling, AbstractBlock.Properties.from(Blocks.POTTED_OAK_SAPLING));
		
		return this;
	}
	
	public String name;
	public WoodType woodType;
	public MaterialColor planksColor;
	public MaterialColor barkColor;
	
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
