package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.incorporeal.datagen.IncRecipes;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.Registry;
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
	protected void buildShapelessRecipes(Consumer<FinishedRecipe> r) {
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
	protected void doWoodFamily(Consumer<FinishedRecipe> r, WoodFamily family) {
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
	protected static void planks(Consumer<FinishedRecipe> recipeConsumer, ItemLike plank, ItemLike log) {
		ShapelessRecipeBuilder.shapeless(plank, 4)
			.requires(log)
			.group("planks")
			.unlockedBy("has_logs", has(log))
			.save(recipeConsumer);
	}
	
	protected static void bark(Consumer<FinishedRecipe> recipeConsumer, ItemLike stripped, ItemLike input) {
		ShapedRecipeBuilder.shaped(stripped, 3)
			.define('#', input)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.unlockedBy("has_log", has(input))
			.save(recipeConsumer);
	}
	
	protected static void button(Consumer<FinishedRecipe> recipeConsumer, ItemLike button, ItemLike input) {
		ShapelessRecipeBuilder.shapeless(button).requires(input).group("wooden_button").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	protected static void door(Consumer<FinishedRecipe> recipeConsumer, ItemLike door, ItemLike input) {
		ShapedRecipeBuilder.shaped(door, 3).define('#', input).pattern("##").pattern("##").pattern("##").group("wooden_door").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void fence(Consumer<FinishedRecipe> recipeConsumer, ItemLike fence, ItemLike input) {
		ShapedRecipeBuilder.shaped(fence, 3).define('#', Items.STICK).define('W', input).pattern("W#W").pattern("W#W").group("wooden_fence").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void fenceGate(Consumer<FinishedRecipe> recipeConsumer, ItemLike fenceGate, ItemLike input) {
		ShapedRecipeBuilder.shaped(fenceGate).define('#', Items.STICK).define('W', input).pattern("#W#").pattern("#W#").group("wooden_fence_gate").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void pressurePlate(Consumer<FinishedRecipe> recipeConsumer, ItemLike pressurePlate, ItemLike input) {
		ShapedRecipeBuilder.shaped(pressurePlate).define('#', input).pattern("##").group("wooden_pressure_plate").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void slab(Consumer<FinishedRecipe> recipeConsumer, ItemLike slab, ItemLike input) {
		ShapedRecipeBuilder.shaped(slab, 6).define('#', input).pattern("###").group("wooden_slab").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void stairs(Consumer<FinishedRecipe> recipeConsumer, ItemLike stairs, ItemLike input) {
		ShapedRecipeBuilder.shaped(stairs, 4).define('#', input).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void trapdoor(Consumer<FinishedRecipe> recipeConsumer, ItemLike trapdoor, ItemLike input) {
		ShapedRecipeBuilder.shaped(trapdoor, 2).define('#', input).pattern("###").pattern("###").group("wooden_trapdoor").unlockedBy("has_planks", has(input)).save(recipeConsumer);
	}
	
	private static void sign(Consumer<FinishedRecipe> recipeConsumer, ItemLike sign, ItemLike input) {
		String s = Registry.ITEM.getKey(input.asItem()).getPath();
		ShapedRecipeBuilder.shaped(sign, 3).group("sign").define('#', input).define('X', Items.STICK).pattern("###").pattern("###").pattern(" X ").unlockedBy("has_" + s, has(input)).save(recipeConsumer);
	}
}
