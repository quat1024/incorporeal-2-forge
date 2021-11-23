package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.item.IncItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IncPetalRecipes extends RecipeProvider {
	public IncPetalRecipes(DataGenerator yea) {
		super(yea);
	}
	
	@Override
	public String getName() {
		return "INcorporeal petal recipes";
	}
	
	//Copypaste kinda
	private static Ingredient tagIngr(String tag) {
		return Ingredient.of(ItemTags.bind(Inc.botaniaId(tag).toString()));
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
		//Copypaste
		Ingredient white = tagIngr("petals/white");
		Ingredient orange = tagIngr("petals/orange");
		Ingredient magenta = tagIngr("petals/magenta");
		Ingredient lightBlue = tagIngr("petals/light_blue");
		Ingredient yellow = tagIngr("petals/yellow");
		Ingredient lime = tagIngr("petals/lime");
		Ingredient pink = tagIngr("petals/pink");
		Ingredient gray = tagIngr("petals/gray");
		Ingredient lightGray = tagIngr("petals/light_gray");
		Ingredient cyan = tagIngr("petals/cyan");
		Ingredient purple = tagIngr("petals/purple");
		Ingredient blue = tagIngr("petals/blue");
		Ingredient brown = tagIngr("petals/brown");
		Ingredient green = tagIngr("petals/green");
		Ingredient red = tagIngr("petals/red");
		Ingredient black = tagIngr("petals/black");
		Ingredient runeWater = tagIngr("runes/water");
		Ingredient runeFire = tagIngr("runes/fire");
		Ingredient runeEarth = tagIngr("runes/earth");
		Ingredient runeAir = tagIngr("runes/air");
		Ingredient runeSpring = tagIngr("runes/spring");
		Ingredient runeSummer = tagIngr("runes/summer");
		Ingredient runeAutumn = tagIngr("runes/autumn");
		Ingredient runeWinter = tagIngr("runes/winter");
		Ingredient runeMana = tagIngr("runes/mana");
		Ingredient runeLust = tagIngr("runes/lust");
		Ingredient runeGluttony = tagIngr("runes/gluttony");
		Ingredient runeGreed = tagIngr("runes/greed");
		Ingredient runeSloth = tagIngr("runes/sloth");
		Ingredient runeWrath = tagIngr("runes/wrath");
		Ingredient runeEnvy = tagIngr("runes/envy");
		Ingredient runePride = tagIngr("runes/pride");
		
		Ingredient redstoneRoot = Ingredient.of(ModItems.redstoneRoot);
		Ingredient pixieDust = Ingredient.of(ModItems.pixieDust);
		Ingredient gaiaSpirit = Ingredient.of(ModItems.lifeEssence);
		
		consumer.accept(new FinishedRecipe(Inc.id("sanvocalia"), new ItemStack(IncItems.SANVOCALIA), white, orange, orange, red, runeLust, pixieDust, redstoneRoot));
		consumer.accept(new FinishedRecipe(Inc.id("funny"), new ItemStack(IncItems.FUNNY), red, orange, yellow, lime, lightBlue, purple, redstoneRoot));
	}
	
	//Copypaste of internal private class from Botania
	public static class FinishedRecipe implements FinishedRecipe {
		private final ResourceLocation id;
		private final ItemStack output;
		private final Ingredient[] inputs;
		
		public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient... inputs) {
			this.id = id;
			this.output = output;
			this.inputs = inputs;
		}
		
		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("output", ItemNBTHelper.serializeStack(output));
			JsonArray ingredients = new JsonArray();
			for (Ingredient ingr : inputs) {
				ingredients.add(ingr.toJson());
			}
			json.add("ingredients", ingredients);
		}
		
		@Override
		public ResourceLocation getId() {
			return id;
		}
		
		@Override
		public RecipeSerializer<?> getType() {
			return ModRecipeTypes.PETAL_SERIALIZER;
		}
		
		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}
		
		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
