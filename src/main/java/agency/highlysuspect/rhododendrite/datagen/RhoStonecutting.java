package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.Registry;
import vazkii.botania.data.recipes.StonecuttingProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RhoStonecutting extends RecipeProvider {
	public RhoStonecutting(DataGenerator gen) {
		super(gen);
	}
	
	@Override
	public String getName() {
		return "RHododendrite stonecutting recipes";
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		stonecutToAny(consumer,
			RhoItems.OPCODE_BLANK,
			RhoItems.OPCODE_PUSH,
			RhoItems.OPCODE_PULL,
			RhoItems.OPCODE_DUP,
			RhoItems.OPCODE_TUMBLE,
			RhoItems.OPCODE_ADD,
			RhoItems.OPCODE_SUBTRACT,
			RhoItems.OPCODE_MULTIPLY,
			RhoItems.OPCODE_DIVIDE,
			RhoItems.OPCODE_REMAINDER,
			RhoItems.OPCODE_MATCHER_UNION,
			RhoItems.OPCODE_MATCHER_DIFFERENCE,
			RhoItems.OPCODE_SWAP
		);
		
		stonecutToAny(consumer,
			RhoItems.CONDITION_BLANK,
			RhoItems.CONDITION_IS_EMPTY,
			RhoItems.CONDITION_IS_ZERO,
			RhoItems.CONDITION_EQUAL,
			RhoItems.CONDITION_COUNT_EQUAL,
			RhoItems.CONDITION_GREATER_THAN,
			RhoItems.CONDITION_LESS_THAN
		);
	}
	
	//A pile of stonecutting recipes where each item can stonecut to all the other items
	private static void stonecutToAny(Consumer<IFinishedRecipe> consumer, IItemProvider... all) {
		List<IItemProvider> inputs = Arrays.asList(all);
		for(IItemProvider output : all) {
			Ingredient input = Ingredient.fromItems(inputs.stream().filter(obj -> output != obj).toArray(IItemProvider[]::new));
			consumer.accept(new StonecuttingProvider.Result(Rho.id("stonecutting/" + Registry.ITEM.getKey(output.asItem()).getPath()), IRecipeSerializer.STONECUTTING, input, output.asItem(), 1));
		}
	}
}
