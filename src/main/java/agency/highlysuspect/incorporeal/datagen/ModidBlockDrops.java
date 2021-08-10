package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;

public class ModidBlockDrops extends BlockLootTables implements IDataProvider  {
	public ModidBlockDrops(String modid, DataGenerator d) {
		this.modid = modid;
		this.d = d;
	}
	
	private final String modid;
	private final DataGenerator d;
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	@Override
	public String getName() {
		return modid + " block drops";
	}
	
	@Override
	public void act(DirectoryCache cache) throws IOException {
		for(Block b : ForgeRegistries.BLOCKS.getValues()) {
			ResourceLocation id = b.getRegistryName(); assert id != null; //FUCKING FORGE
			if(!id.getNamespace().equals(modid)) continue;
			
			if(b instanceof WallSignBlock) continue; //by convention these all "dropsLike" the regular block, no need for an extra table
			if(b == RhoBlocks.AWAKENED_LOG) continue;
			
			if(b instanceof DoorBlock) {
				save(cache, b, door(b));
			} else if(b instanceof SlabBlock) {
				save(cache, b, slab(b));
			} else if(b instanceof LeavesBlock) {
				Preconditions.checkArgument(b.getRegistryName().equals(Rho.id("rhododendrite_leaves")), "fix ur shit");
				save(cache, b, leaves(b, RhoBlocks.RHODODENDRITE.planks)); //TODO actually drop the sapling when i add that.
			} else {
				//all other blocks drop themselves
				save(cache, b, self(b));
			}
		}
	}
	
	private void save(DirectoryCache cache, Block b, LootTable table) throws IOException {
		ResourceLocation id = b.getRegistryName(); assert id != null; //im gonna kms for real
		
		Path out = d.getOutputFolder().resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
		IDataProvider.save(GSON, cache, LootTableManager.toJson(table), out);
	}
	
	private static LootTable self(Block b) {
		return LootTable.builder().addLootPool(
			LootPool.builder().name("self").rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(b)).acceptCondition(SurvivesExplosion.builder())
		).setParameterSet(LootParameterSets.BLOCK).build();
	}
	
	private static LootTable door(Block b) {
		return BlockLootTables.registerDoor(b).setParameterSet(LootParameterSets.BLOCK).build();
	}
	
	private static LootTable slab(Block b) {
		return droppingSlab(b).setParameterSet(LootParameterSets.BLOCK).build();
	}
	
	private static LootTable leaves(Block b, Block sapling) {
		return droppingWithChancesAndSticks(b, sapling, 0.05F, 0.0625F, 0.083333336F, 0.1F).setParameterSet(LootParameterSets.BLOCK).build();
	}
}
