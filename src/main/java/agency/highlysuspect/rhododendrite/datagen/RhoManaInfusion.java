package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.incorporeal.datagen.IncManaInfusion;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

public class RhoManaInfusion extends IncManaInfusion {
	public RhoManaInfusion(DataGenerator pee) {
		super(Rho.MODID, pee);
	}
	
	@Override
	public String getName() {
		return "Rho rune recipes";
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
//		cycle(consumer, 20, "rhododendrite:opcode_cycle",
//			RhoItems.OPCODE_BLANK,
//			RhoItems.OPCODE_PUSH,
//			RhoItems.OPCODE_PULL,
//			RhoItems.OPCODE_DUP,
//			RhoItems.OPCODE_TUMBLE,
//			RhoItems.OPCODE_ADD,
//			RhoItems.OPCODE_SUBTRACT,
//			RhoItems.OPCODE_MULTIPLY,
//			RhoItems.OPCODE_DIVIDE,
//			RhoItems.OPCODE_REMAINDER,
//			RhoItems.OPCODE_MATCHER_UNION,
//			RhoItems.OPCODE_MATCHER_DIFFERENCE,
//			RhoItems.OPCODE_SWAP
//		);
//		
//		cycle(consumer, 20, "rhododendrite:condition_cycle",
//			RhoItems.CONDITION_BLANK,
//			RhoItems.CONDITION_IS_EMPTY,
//			RhoItems.CONDITION_IS_ZERO,
//			RhoItems.CONDITION_EQUAL,
//			RhoItems.CONDITION_COUNT_EQUAL,
//			RhoItems.CONDITION_GREATER_THAN,
//			RhoItems.CONDITION_LESS_THAN
//		);
	}
}
