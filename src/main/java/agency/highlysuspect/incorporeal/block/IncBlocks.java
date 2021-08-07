package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Init;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.common.block.ModBlocks;

public class IncBlocks {
	public static final Block CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(AbstractBlock.Properties.from(ModBlocks.corporeaRetainer));
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		Init.reg(r, "corporea_solidifier", CORPOREA_SOLIDIFIER);
	}
}
