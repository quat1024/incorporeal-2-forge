package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.crafting.StateIngredientHelper;
import vazkii.botania.data.recipes.ManaInfusionProvider;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IncManaInfusion extends RecipeProvider {
	public IncManaInfusion(String modid, DataGenerator bwbwbuwbubwuw) {
		super(bwbwbuwbubwuw);
		this.modid = modid;
	}
	
	public IncManaInfusion(DataGenerator ajsdlkasd) {
		this(Inc.MODID, ajsdlkasd);
	}
	
	//having a great time writing maintainable, easy to understand code, on my computer
	protected final String modid;
	
	@Override
	public String getName() {
		return "INCORPOREAL MANA INFUSIO!N!!!!";
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		consumer.accept(mini(IncBlocks.SMALL_SANVOCALIA, IncBlocks.SANVOCALIA));
		consumer.accept(mini(IncBlocks.SMALL_FUNNY, IncBlocks.FUNNY));
	}
	
	//Copypaste
	public FinishedRecipe mini(IItemProvider mini, IItemProvider full) {
		return FinishedRecipe.alchemy(id(Registry.ITEM.getKey(mini.asItem()).getPath()), new ItemStack(mini), ingr(full), 2500, "botania:flower_shrinking");
	}
	
	public ResourceLocation id(String s) {
		return new ResourceLocation(modid, "mana_infusion/" + s);
	}
	
	public Ingredient ingr(IItemProvider i) {
		return Ingredient.fromItems(i);
	}
	
	public void cycle(Consumer<IFinishedRecipe> consumer, int cost, String group, IItemProvider... items) {
		for (int i = 0; i < items.length; i++) {
			Ingredient in = ingr(items[i]);
			ItemStack out = new ItemStack(i == items.length - 1 ? items[0] : items[i + 1]);
			String id = String.format("%s_to_%s", Registry.ITEM.getKey(items[i].asItem()).getPath(), Registry.ITEM.getKey(out.getItem()).getPath());
			consumer.accept(FinishedRecipe.alchemy(id(id), out, in, cost, group));
		}
	}
	
	//Copypaste
	public static class FinishedRecipe implements IFinishedRecipe {
		private static final StateIngredient CONJURATION = StateIngredientHelper.of(ModBlocks.conjurationCatalyst);
		private static final StateIngredient ALCHEMY = StateIngredientHelper.of(ModBlocks.alchemyCatalyst);
		
		private final ResourceLocation id;
		private final Ingredient input;
		private final ItemStack output;
		private final int mana;
		private final String group;
		@Nullable
		private final StateIngredient catalyst;
		
		public static FinishedRecipe conjuration(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
			return new FinishedRecipe(id, output, input, mana, "", CONJURATION);
		}
		
		public static FinishedRecipe alchemy(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
			return alchemy(id, output, input, mana, "");
		}
		
		public static FinishedRecipe alchemy(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group) {
			return new FinishedRecipe(id, output, input, mana, group, ALCHEMY);
		}
		
		public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
			this(id, output, input, mana, "");
		}
		
		public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group) {
			this(id, output, input, mana, group, null);
		}
		
		public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group, @Nullable StateIngredient catalyst) {
			this.id = id;
			this.input = input;
			this.output = output;
			this.mana = mana;
			this.group = group;
			this.catalyst = catalyst;
		}
		
		@Override
		public void serialize(JsonObject json) {
			json.add("input", input.serialize());
			json.add("output", ItemNBTHelper.serializeStack(output));
			json.addProperty("mana", mana);
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}
			if (catalyst != null) {
				json.add("catalyst", catalyst.serialize());
			}
		}
		
		@Override
		public ResourceLocation getID() {
			return id;
		}
		
		@Override
		public IRecipeSerializer<?> getSerializer() {
			return ModRecipeTypes.MANA_INFUSION_SERIALIZER;
		}
		
		@Nullable
		@Override
		public JsonObject getAdvancementJson() {
			return null;
		}
		
		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return null;
		}
	}
}
