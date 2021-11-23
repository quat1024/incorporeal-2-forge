package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.incorporeal.datagen.IncRuneRecipes;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

import agency.highlysuspect.incorporeal.datagen.IncRuneRecipes.FinishedRecipe;

public class RhoRuneRecipes extends IncRuneRecipes {
	public RhoRuneRecipes(DataGenerator pee) {
		super(pee);
	}
	
	@Override
	public String getName() {
		return "rhododend rune recipes";
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new FinishedRecipe(
			Rho.id("temp_recipe_to_make_the_sapling_lololol"),
			new ItemStack(RhoBlocks.RHODODENDRITE.sapling),
			5200,
			Ingredient.of(ItemTags.SAPLINGS),
			Ingredient.of(Items.PINK_DYE),
			Ingredient.of(IncItems.SOUL_CORE_FRAME)
		));
	}
}
