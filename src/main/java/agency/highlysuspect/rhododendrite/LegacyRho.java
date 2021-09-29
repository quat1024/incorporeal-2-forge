package agency.highlysuspect.rhododendrite;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;

public class LegacyRho {
	//TODO this is not on any event bus. idk which one it's fired on, will need to check.
	public static void missingMappings(RegistryEvent.MissingMappings<TileEntityType<?>> event) {
		for(RegistryEvent.MissingMappings.Mapping<TileEntityType<?>> map : event.getMappings(Rho.MODID)) {
			switch(map.key.getPath()) {
				case "core":
				case "awakened_log":
				case "opcode":
				case "condition":
				case "funnel":
					map.ignore();
				default: //nothin
			}
		}
	}
	
	//		Rho.reg(r, "core", CORE);
//		Rho.reg(r, "awakened_log", AWAKENED_LOG);
//		
//		Rho.reg(r, "opcode", OPCODE);
//		Rho.reg(r, "condition", CONDITION);
//		Rho.reg(r, "funnel", FUNNEL);             //items too
	
	
	//Also i removed a couple recipes? idk if those need missing mappings event anymore
}
