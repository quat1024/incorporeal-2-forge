package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.incorporeal.datagen.IncRecipes;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.Registry;
import vazkii.botania.common.block.ModFluffBlocks;

import java.util.function.Consumer;

public class RhoRecipes extends IncRecipes {
	public RhoRecipes(DataGenerator gen) {
		super(gen);
	}
	
	@Override
	public String getName() {
		return "Rhododendrite recipes";
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> r) {
		doWoodFamily(r, RhoBlocks.RHODODENDRITE);
		
		shapeless(RhoItems.OPCODE_BLANK, 32)
			.requires(Items.CRAFTING_TABLE)
			.requires(ModFluffBlocks.biomeStoneMountain)
			.unlockedBy("has_rhodo", has(RhoBlocks.RHODODENDRITE.log))
			.unlockedBy("has_ugly_stone", has(ModFluffBlocks.biomeStoneMountain))
			.save(r);
		
		shapeless(RhoItems.CONDITION_BLANK, 32)
			.requires(Items.CRAFTING_TABLE)
			.requires(ModFluffBlocks.biomeStoneForest)
			.unlockedBy("has_rhodo", has(RhoBlocks.RHODODENDRITE.log))
			.unlockedBy("has_ugly_stone", has(ModFluffBlocks.biomeStoneForest))
			.save(r);

//		shaped(RhoBlocks.OPCODE, 1, "#S#", "#X#", "###")
//			.key('S', ModItems.corporeaSpark)
//			.key('X', ModBlocks.corporeaBlock)
//			.key('#', RhoBlocks.RHODODENDRITE.planks)
//			.addCriterion("has_rhodo", hasItem(RhoBlocks.RHODODENDRITE.log))
//			.build(r);
//		
//		shaped(RhoBlocks.CONDITION, 1, "#S#", "#X#", "###")
//			.key('S', ModItems.corporeaSpark)
//			.key('X', ModItems.elfQuartz)
//			.key('#', RhoBlocks.RHODODENDRITE.planks)
//			.addCriterion("has_rhodo", hasItem(RhoBlocks.RHODODENDRITE.log))
//			.build(r);
//		
//		shaped(RhoBlocks.FUNNEL, 1, "#S#", "#X#", "###")
//			.key('S', ModItems.corporeaSpark)
//			.key('X', Blocks.DROPPER)
//			.key('#', RhoBlocks.RHODODENDRITE.planks)
//			.addCriterion("has_rhodo", hasItem(RhoBlocks.RHODODENDRITE.log))
//			.build(r);
	}
	
	@SuppressWarnings("SameParameterValue")
	protected void doWoodFamily(Consumer<IFinishedRecipe> r, WoodFamily family) {
		//ok because i dont have item tags set up, the recipe gen tries to make 2 recipes named "rhododendrite_planks"
		//and blows up. its just because i cant change the name of the recipe >.>
		//totally valid to have two recipes that give the same item but Nooo..
		//anyway TODO (issue #1) just make stripped logs not give planks and hope noone notices until i fix it
		planks(r, family.planks, family.log);
		//planks(r, family.planks, family.strippedLog);
		
		bark(r, family.wood, family.log);
		bark(r, family.strippedWood, family.strippedLog);
		
		button(r, family.button, family.planks);
		//door(r, family.door, family.planks); //TODO (issue #1)
		fence(r, family.fence, family.planks);
		fenceGate(r, family.fenceGate, family.planks);
		pressurePlate(r, family.pressurePlate, family.planks);
		slab(r, family.slab, family.planks);
		stairs(r, family.stairs, family.planks);
		//trapdoor(r, family.trapdoor, family.planks); //TODO (issue #1)
		//sign(r, family.sign, family.planks); //TODO (issue #1)
		//boat //TODO (issue #1)
	}
	
	//Most of these are copy pasta from vanilla because they're all PRIVATE
	//changed a lot of names because mcp has em wrong lol
	protected static void planks(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider plank, IItemProvider log) {
		ShapelessRecipeBuilder.shapeless(plank, 4)
			.requires(log)
			.group("planks")
			.unlockedBy("has_logs", has(log))
			.save(recipeConsumer);
	}
	
	protected static void bark(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stripped, IItemProvider input) {
		ShapedRecipeBuilder.shaped(stripped, 3)
			.define('#', input)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.unlockedBy("has_log", has(input))
			.save(recipeConsumer);
	}
	
	protected static void button(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider button, IItemProvider input) {
		ShapelessRecipeBuilder.shapeless(button).requires(input).group("wooden_button").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	protected static void door(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider door, IItemProvider input) {
		ShapedRecipeBuilder.shaped(door, 3).define('#', input).pattern("##").pattern("##").pattern("##").group("wooden_door").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void fence(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fence, IItemProvider input) {
		ShapedRecipeBuilder.shaped(fence, 3).define('#', Items.STICK).define('W', input).pattern("W#W").pattern("W#W").group("wooden_fence").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void fenceGate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fenceGate, IItemProvider input) {
		ShapedRecipeBuilder.shaped(fenceGate).define('#', Items.STICK).define('W', input).pattern("#W#").pattern("#W#").group("wooden_fence_gate").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void pressurePlate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider pressurePlate, IItemProvider input) {
		ShapedRecipeBuilder.shaped(pressurePlate).define('#', input).pattern("##").group("wooden_pressure_plate").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void slab(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider slab, IItemProvider input) {
		ShapedRecipeBuilder.shaped(slab, 6).define('#', input).pattern("###").group("wooden_slab").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void stairs(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stairs, IItemProvider input) {
		ShapedRecipeBuilder.shaped(stairs, 4).define('#', input).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void trapdoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider trapdoor, IItemProvider input) {
		ShapedRecipeBuilder.shaped(trapdoor, 2).define('#', input).pattern("###").pattern("###").group("wooden_trapdoor").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void sign(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider sign, IItemProvider input) {
		String s = Registry.ITEM.getKey(input.asItem()).getPath();
		ShapedRecipeBuilder.shaped(sign, 3).group("sign").define('#', input).define('X', Items.STICK).pattern("###").pattern("###").pattern(" X ").unlockedBy("has_" + s, has(input)).save(recipeConsumer);
	}
}
