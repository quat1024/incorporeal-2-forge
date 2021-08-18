package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.incorporeal.datagen.IncRuneRecipes;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class RhoRuneRecipes extends IncRuneRecipes {
	public RhoRuneRecipes(DataGenerator pee) {
		super(pee);
	}
	
	@Override
	public String getName() {
		return "rhododend rune recipes";
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		consumer.accept(new FinishedRecipe(
			Rho.id("temp_recipe_to_make_the_sapling_lololol"),
			new ItemStack(RhoBlocks.RHODODENDRITE.sapling),
			5200,
			Ingredient.fromTag(ItemTags.SAPLINGS),
			Ingredient.fromItems(Items.PINK_DYE),
			Ingredient.fromItems(IncItems.SOUL_CORE_FRAME)
		));
	}
}
