package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.item.IncItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IncRuneRecipes extends RecipeProvider {
	public IncRuneRecipes(DataGenerator pee) {
		super(pee);
	}
	
	@Override
	public String getName() {
		return "incorporeal rune recipes";
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		final int costTier1 = 5200;
		final int costTier2 = 8000;
		final int costTier3 = 12000;//Pasted
		
		Ingredient ice = Ingredient.of(Blocks.ICE);
		Ingredient pixieDust = Ingredient.of(ModItems.pixieDust);
		
		consumer.accept(new FinishedRecipe(
			Inc.id("soul_core_frame"), new ItemStack(IncItems.SOUL_CORE_FRAME),
			costTier3,
			ice, ice, ice, ice, pixieDust,
			ice, ice, ice, ice, pixieDust
		));
		
		soulCore(consumer, Inc.id("ender_soul_core"), costTier3 * 2, new ItemStack(IncItems.ENDER_SOUL_CORE), Ingredient.of(ModItems.enderHand));
		soulCore(consumer, Inc.id("corporea_soul_core"), 300, new ItemStack(IncItems.CORPOREA_SOUL_CORE), Ingredient.of(ModItems.corporeaSpark));
		soulCore(consumer, Inc.id("potion_soul_core"), costTier3 * 2, new ItemStack(IncItems.POTION_SOUL_CORE), Ingredient.of(ModItems.bloodPendant));
	}
	
	protected void soulCore(Consumer<IFinishedRecipe> consumer, ResourceLocation id, int price, ItemStack output, Ingredient special) {
		Ingredient frame = Ingredient.of(IncItems.SOUL_CORE_FRAME);
		Ingredient dragonStone = Ingredient.of(ModTags.Items.GEMS_DRAGONSTONE);
		Ingredient manaweave = Ingredient.of(ModItems.manaweaveCloth);
		
		consumer.accept(new FinishedRecipe(id, output,
			price,
			frame,
			dragonStone, dragonStone,
			manaweave, manaweave,
			special
		));
	}
	
	//Copypaste of internal private class from Botania
	public static class FinishedRecipe implements IFinishedRecipe {
		private final ResourceLocation id;
		private final ItemStack output;
		private final int mana;
		private final Ingredient[] inputs;
		
		public FinishedRecipe(ResourceLocation id, ItemStack output, int mana, Ingredient... inputs) {
			this.id = id;
			this.output = output;
			this.mana = mana;
			this.inputs = inputs;
		}
		
		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("output", ItemNBTHelper.serializeStack(output));
			JsonArray ingredients = new JsonArray();
			for (Ingredient ingr : inputs) {
				ingredients.add(ingr.toJson());
			}
			json.addProperty("mana", mana);
			json.add("ingredients", ingredients);
		}
		
		@Override
		public ResourceLocation getId() {
			return id;
		}
		
		@Override
		public IRecipeSerializer<?> getType() {
			return ModRecipeTypes.RUNE_SERIALIZER;
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
