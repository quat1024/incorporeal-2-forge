package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.loot.*;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class ModidBlockDrops extends BlockLoot implements DataProvider  {
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
	public void run(HashCache cache) throws IOException {
		for(Block b : ForgeRegistries.BLOCKS.getValues()) {
			ResourceLocation id = b.getRegistryName(); assert id != null; //FUCKING FORGE
			if(!id.getNamespace().equals(modid)) continue;
			
			if(b instanceof WallSignBlock) continue; //by convention these all "dropsLike" the regular block, no need for an extra table
			if(b instanceof CropBlock) continue; //no u
			
			if(b instanceof DoorBlock) {
				save(cache, b, door(b));
			} else if(b instanceof SlabBlock) {
				save(cache, b, slab(b));
			} else if(b instanceof LeavesBlock) {
				Preconditions.checkArgument(b.getRegistryName().equals(Rho.id("rhododendrite_leaves")), "fix ur shit");
				save(cache, b, leaves(b, RhoBlocks.RHODODENDRITE.sapling));
			} else {
				//all other blocks drop themselves
				save(cache, b, self(b));
			}
		}
	}
	
	private void save(HashCache cache, Block b, LootTable table) throws IOException {
		ResourceLocation id = b.getRegistryName(); assert id != null; //im gonna kms for real
		
		Path out = d.getOutputFolder().resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
		DataProvider.save(GSON, cache, LootTables.serialize(table), out);
	}
	
	private static LootTable self(Block b) {
		return LootTable.lootTable().withPool(
			LootPool.lootPool().name("self").setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(b)).when(ExplosionCondition.survivesExplosion())
		).setParamSet(LootContextParamSets.BLOCK).build();
	}
	
	private static LootTable door(Block b) {
		return BlockLoot.createDoorTable(b).setParamSet(LootContextParamSets.BLOCK).build();
	}
	
	private static LootTable slab(Block b) {
		return createSlabItemTable(b).setParamSet(LootContextParamSets.BLOCK).build();
	}
	
	private static LootTable leaves(Block b, Block sapling) {
		return createLeavesDrops(b, sapling, 0.05F, 0.0625F, 0.083333336F, 0.1F).setParamSet(LootContextParamSets.BLOCK).build();
	}
}
