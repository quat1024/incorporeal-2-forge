package agency.highlysuspect.incorporeal.datagen.data;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.datagen.DataGenerators;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;

public class BlockDrops implements IDataProvider {
	public BlockDrops(DataGenerator d) {
		this.d = d;
	}
	
	private final DataGenerator d;
	
	@Override
	public String getName() {
		return "incorporeal block drops";
	}
	
	@Override
	public void act(DirectoryCache cache) throws IOException {
		for(Block b : ForgeRegistries.BLOCKS.getValues()) {
			ResourceLocation id = b.getRegistryName();
			assert id != null; //FUCKING FORGE
			if(!id.getNamespace().equals(Init.MODID)) continue;
			
			//all blocks drop themselves
			save(cache, b, self(b));
		}
	}
	
	private void save(DirectoryCache cache, Block b, LootTable table) throws IOException {
		ResourceLocation id = b.getRegistryName(); assert id != null; //im gonna kms for real
		
		Path out = d.getOutputFolder().resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
		IDataProvider.save(DataGenerators.GSON, cache, LootTableManager.toJson(table), out);
	}
	
	private static LootTable self(Block b) {
		return LootTable.builder().addLootPool(
			LootPool.builder().name("self").rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(b)).acceptCondition(SurvivesExplosion.builder())
		).setParameterSet(LootParameterSets.BLOCK).build();
	}
}
