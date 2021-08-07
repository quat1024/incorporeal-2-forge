package agency.highlysuspect.incorporeal;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.common.lib.LibMisc;

@Mod("incorporeal")
public class Init {
	public static final String MODID = "incorporeal";
	public static final Logger LOG = LogManager.getLogger(MODID);
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static ResourceLocation botaniaId(String path) {
		return new ResourceLocation(LibMisc.MOD_ID, path);
	}
}
