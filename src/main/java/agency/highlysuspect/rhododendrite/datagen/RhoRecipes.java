package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.incorporeal.datagen.IncRecipes;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.Registry;

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
	protected void registerRecipes(Consumer<IFinishedRecipe> r) {
		doWoodFamily(r, RhoBlocks.RHODODENDRITE);
	}
	
	@SuppressWarnings("SameParameterValue")
	protected void doWoodFamily(Consumer<IFinishedRecipe> r, WoodFamily family) {
		//ok because i dont have item tags set up, the recipe gen tries to make 2 recipes named "rhododendrite_planks"
		//and blows up. its just because i cant change the name of the recipe >.>
		//totally valid to have two recipes that give the same item but Nooo..
		//anyway TODO just make stripped logs not give planks and hope noone notices until i fix it
		planks(r, family.planks, family.log);
		//planks(r, family.planks, family.strippedLog);
		
		bark(r, family.wood, family.log);
		bark(r, family.strippedWood, family.strippedLog);
		
		button(r, family.button, family.planks);
		door(r, family.door, family.planks);
		fence(r, family.fence, family.planks);
		fenceGate(r, family.fenceGate, family.planks);
		pressurePlate(r, family.pressurePlate, family.planks);
		slab(r, family.slab, family.planks);
		stairs(r, family.stairs, family.planks);
		trapdoor(r, family.trapdoor, family.planks);
		//sign(r, family.sign, family.planks); //TODO
		//boat //TODO
	}
	
	//Most of these are copy pasta from vanilla because they're all PRIVATE
	//changed a lot of names because mcp has em wrong lol
	protected static void planks(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider plank, IItemProvider log) {
		ShapelessRecipeBuilder.shapelessRecipe(plank, 4)
			.addIngredient(log)
			.setGroup("planks")
			.addCriterion("has_logs", hasItem(log))
			.build(recipeConsumer);
	}
	
	protected static void bark(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stripped, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(stripped, 3)
			.key('#', input)
			.patternLine("##")
			.patternLine("##")
			.setGroup("bark")
			.addCriterion("has_log", hasItem(input))
			.build(recipeConsumer);
	}
	
	protected static void button(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider button, IItemProvider input) {
		ShapelessRecipeBuilder.shapelessRecipe(button).addIngredient(input).setGroup("wooden_button").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	protected static void door(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider door, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(door, 3).key('#', input).patternLine("##").patternLine("##").patternLine("##").setGroup("wooden_door").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	private static void fence(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fence, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(fence, 3).key('#', Items.STICK).key('W', input).patternLine("W#W").patternLine("W#W").setGroup("wooden_fence").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	private static void fenceGate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fenceGate, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(fenceGate).key('#', Items.STICK).key('W', input).patternLine("#W#").patternLine("#W#").setGroup("wooden_fence_gate").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	private static void pressurePlate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider pressurePlate, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(pressurePlate).key('#', input).patternLine("##").setGroup("wooden_pressure_plate").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	private static void slab(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider slab, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(slab, 6).key('#', input).patternLine("###").setGroup("wooden_slab").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	private static void stairs(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stairs, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(stairs, 4).key('#', input).patternLine("#  ").patternLine("## ").patternLine("###").setGroup("wooden_stairs").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	private static void trapdoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider trapdoor, IItemProvider input) {
		ShapedRecipeBuilder.shapedRecipe(trapdoor, 2).key('#', input).patternLine("###").patternLine("###").setGroup("wooden_trapdoor").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
	}
	
	private static void sign(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider sign, IItemProvider input) {
		String s = Registry.ITEM.getKey(input.asItem()).getPath();
		ShapedRecipeBuilder.shapedRecipe(sign, 3).setGroup("sign").key('#', input).key('X', Items.STICK).patternLine("###").patternLine("###").patternLine(" X ").addCriterion("has_" + s, hasItem(input)).build(recipeConsumer);
	}
}
