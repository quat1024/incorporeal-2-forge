package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

public class IncRecipes extends RecipeProvider {
	public IncRecipes(DataGenerator gen) {
		super(gen);
	}
	
	@Override
	public String getName() {
		return "Incorporeal recipes";
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<FinishedRecipe> r) {
		for(UnstableCubeBlock cube : IncBlocks.UNSTABLE_CUBES.values()) {
			unstableCube(cube).save(r);
		}
		
		ticketConjurer().save(r);
		redStringLiar().save(r);
		frameTinkerer().save(r);
		rodOfTheFracturedSpace().save(r);
		corporeaSolidifier().save(r);
		corporeaRetainerEvaporator().save(r);
		
		createFloatingFlowerRecipe(r, IncItems.SANVOCALIA);
		createFloatingFlowerRecipe(r, IncItems.FUNNY);
		createFloatingFlowerRecipe(r, IncItems.SMALL_SANVOCALIA);
		createFloatingFlowerRecipe(r, IncItems.SMALL_FUNNY);
	}
	
	//Copypaste
	private void createFloatingFlowerRecipe(Consumer<FinishedRecipe> consumer, ItemLike input) {
		ResourceLocation inputName = Registry.ITEM.getKey(input.asItem());
		Item output = Registry.ITEM.getOptional(new ResourceLocation(inputName.getNamespace(), "floating_" + inputName.getPath())).get();
		ShapelessRecipeBuilder.shapeless(output)
			.requires(ModTags.Items.FLOATING_FLOWERS)
			.requires(input)
			.unlockedBy("has_item", has(input))
			.save(consumer);
	}
	
	protected ShapelessRecipeBuilder shapeless(ItemLike item, int count) {
		return ShapelessRecipeBuilder.shapeless(item, count);
	}
	
	protected ShapedRecipeBuilder shaped(ItemLike item, int count, String... lines) {
		ShapedRecipeBuilder shaped = ShapedRecipeBuilder.shaped(item, count);
		for(String line : lines) shaped.pattern(line);
		return shaped;
	}
	
	protected ShapedRecipeBuilder corporeaRetainerEvaporator() {
		ShapedRecipeBuilder builder = shaped(IncBlocks.CORPOREA_RETAINER_EVAPORATOR, 1, "P P", "PCP", "P P"); //heehee pcp
		item(builder, 'P', ModFluffBlocks.bluePavement);
		item(builder, 'C', ModItems.corporeaSpark);
		return builder;
	}
	
	protected ShapedRecipeBuilder corporeaSolidifier() {
		ShapedRecipeBuilder builder = shaped(IncBlocks.CORPOREA_SOLIDIFIER, 1, "PPP", "EFE", "PPP");
		item(builder, 'P', Items.PAPER);
		item(builder, 'F', ModBlocks.corporeaFunnel);
		item(builder, 'E', ModTags.Items.INGOTS_ELEMENTIUM);
		return builder;
	}
	
	protected ShapedRecipeBuilder rodOfTheFracturedSpace() {
		ShapedRecipeBuilder builder = shaped(IncItems.FRACTURED_SPACE_ROD, 1)
			.pattern(" EG")
			.pattern(" TE")
			.pattern("T  ");
		
		item(builder, 'E', Items.ENDER_EYE);
		item(builder, 'G', ModItems.lifeEssence); //gaia spirit
		item(builder, 'T', ModItems.dreamwoodTwig);
		return builder;
	}
	
	protected ShapedRecipeBuilder frameTinkerer() {
		ShapedRecipeBuilder builder = shaped(IncBlocks.FRAME_TINKERER, 1, "SLS", "LFL");
		item(builder, 'S', ModTags.Items.INGOTS_MANASTEEL);
		item(builder, 'L', ModTags.Items.LIVINGROCK);
		item(builder, 'F', Items.ITEM_FRAME);
		return builder;
	}
	
	protected ShapedRecipeBuilder redStringLiar() {
		ShapedRecipeBuilder builder = shaped(IncBlocks.RED_STRING_LIAR, 1, "RRR", "RCS", "RRR");
		item(builder, 'R', ModTags.Items.LIVINGROCK);
		item(builder, 'S', ModItems.redString);
		item(builder, 'C', ModBlocks.forestEye);
		return builder;
	}
	
	protected ShapedRecipeBuilder ticketConjurer() {
		ShapedRecipeBuilder builder = shaped(IncItems.TICKET_CONJURER, 1, "SES", "EIE", "SES");
		item(builder, 'S', ModTags.Items.INGOTS_MANASTEEL);
		item(builder, 'E', ModTags.Items.INGOTS_ELEMENTIUM);
		item(builder, 'I', ModBlocks.corporeaIndex);
		return builder;
	}
	
	protected ShapedRecipeBuilder unstableCube(UnstableCubeBlock cube) {
		Item petal = ModItems.getPetal(cube.color);
		
		ShapedRecipeBuilder builder = shaped(cube, 2, "OPO", "PEP", "OPO").group("unstable_cube");
		item(builder, 'O', Blocks.OBSIDIAN);
		item(builder, 'P', petal);
		item(builder, 'E', Items.ENDER_PEARL);
		return builder;
	}
	
	protected void item(ShapedRecipeBuilder builder, Character key, ItemLike in) {
		builder.define(key, in);
		builder.unlockedBy(in.asItem().getRegistryName().getPath(), has(in));
	}
	
	protected void item(ShapedRecipeBuilder builder, Character key, Tag.Named<Item> in) {
		builder.define(key, in);
		builder.unlockedBy(in.getName().getPath(), has(in));
	}
}
