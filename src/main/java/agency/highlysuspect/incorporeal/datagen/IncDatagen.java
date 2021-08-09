package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.Collections;

//feels kind of weird to ship these with the mod, but alright
public class IncDatagen {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static void gatherData(GatherDataEvent e) {
		ExistingFileHelper helper = e.getExistingFileHelper();
		DataGenerator data = e.getGenerator();
		
		//Forge has this misfeature where it checks that textures/models/etc referenced in files exist.
		//I don't... really give two shits about this? While I'm still *setting up* the system, I'd rather generate half-broken models than, well, nothing?
		//If the models have an error, the game will tell me that when loading the models. That's how it's always worked
		//anyway lick my tailhole forge
		//i think theres a command line argument for this but im doing this out of spite for this shitass system
		//thanks for spending time on that and not like, any scaffolding for setting up block loot tables
		@SuppressWarnings("deprecation")
		ExistingFileHelper dontCare = new ExistingFileHelper(Collections.emptySet(), false);
		
		if(e.includeServer()) {
			data.addProvider(new ModidBlockDrops(Inc.MODID, data));
		}
		
		if(e.includeClient()) {
			data.addProvider(new IncStatesModels(data, dontCare));
		}
	}
}
