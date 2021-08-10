package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.incorporeal.datagen.ModidBlockDrops;
import agency.highlysuspect.rhododendrite.Rho;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.Collections;

public class RhoDatagen {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static void gatherData(GatherDataEvent e) {
		DataGenerator data = e.getGenerator();
		
		@SuppressWarnings("deprecation")
		ExistingFileHelper dontCare = new ExistingFileHelper(Collections.emptySet(), false);
		
		if(e.includeServer()) {
			//TODO: the wall sign is the 1 block that doesn't drop itself
			//so i will actually need to use a custom datagen instead of playing off Incorporeal's
			data.addProvider(new ModidBlockDrops(Rho.MODID, data));
			data.addProvider(new RhoRecipes(data));
			
			RhoTags.BlockProvider b = new RhoTags.BlockProvider(data, dontCare);
			data.addProvider(b);
			data.addProvider(new RhoTags.ItemProvider(data, b, dontCare));
		}
		
		if(e.includeClient()) {
			data.addProvider(new RhoStatesModels(data, dontCare));
		}
	}
}
