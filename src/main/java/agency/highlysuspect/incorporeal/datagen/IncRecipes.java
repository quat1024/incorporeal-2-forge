package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

import java.util.function.Consumer;

public class IncRecipes extends RecipeProvider {
	public IncRecipes(DataGenerator gen) {
		super(gen);
	}
	
	@Override
	public String getName() {
		return "Incorporeal recipes";
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> r) {
		for(UnstableCubeBlock cube : IncBlocks.UNSTABLE_CUBES.values()) {
			unstableCube(cube).build(r);
		}
		
		ticketConjurer().build(r);
		redStringLiar().build(r);
		frameTinkerer().build(r);
		rodOfTheFracturedSpace().build(r);
		corporeaSolidifier().build(r);
		corporeaRetainerEvaporator().build(r);
		
		createFloatingFlowerRecipe(r, IncItems.SANVOCALIA);
		createFloatingFlowerRecipe(r, IncItems.FUNNY);
		createFloatingFlowerRecipe(r, IncItems.SMALL_SANVOCALIA);
		createFloatingFlowerRecipe(r, IncItems.SMALL_FUNNY);
	}
	
	//Copypaste
	private void createFloatingFlowerRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider input) {
		ResourceLocation inputName = Registry.ITEM.getKey(input.asItem());
		Item output = Registry.ITEM.getOptional(new ResourceLocation(inputName.getNamespace(), "floating_" + inputName.getPath())).get();
		ShapelessRecipeBuilder.shapelessRecipe(output)
			.addIngredient(ModTags.Items.FLOATING_FLOWERS)
			.addIngredient(input)
			.addCriterion("has_item", hasItem(input))
			.build(consumer);
	}
	
	protected ShapedRecipeBuilder shaped(IItemProvider item, int count, String... lines) {
		ShapedRecipeBuilder shaped = ShapedRecipeBuilder.shapedRecipe(item, count);
		for(String line : lines) shaped.patternLine(line);
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
			.patternLine(" EG")
			.patternLine(" TE")
			.patternLine("T  ");
		
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
		
		ShapedRecipeBuilder builder = shaped(cube, 2, "OPO", "PEP", "OPO").setGroup("unstable_cube");
		item(builder, 'O', Blocks.OBSIDIAN);
		item(builder, 'P', petal);
		item(builder, 'E', Items.ENDER_PEARL);
		return builder;
	}
	
	protected void item(ShapedRecipeBuilder builder, Character key, IItemProvider in) {
		builder.key(key, in);
		builder.addCriterion(in.asItem().getRegistryName().getPath(), hasItem(in));
	}
	
	protected void item(ShapedRecipeBuilder builder, Character key, ITag.INamedTag<Item> in) {
		builder.key(key, in);
		builder.addCriterion(in.getName().getPath(), hasItem(in));
	}
}
