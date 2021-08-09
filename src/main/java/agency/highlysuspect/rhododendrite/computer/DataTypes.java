package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.rhododendrite.Rho;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Lifecycle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import java.math.BigInteger;

public class DataTypes {
	public static final RegistryKey<Registry<DataType<?>>> DATATYPES_KEY = RegistryKey.getOrCreateRootKey(Rho.id("datatypes"));
	public static final Registry<DataType<?>> REGISTRY = new SimpleRegistry<>(DATATYPES_KEY, Lifecycle.stable());
	
	public static final DataType<Unit> EMPTY = new EmptyType();
	public static final DataType<ItemStack> ITEM_STACK = new ItemStackType();
	public static final DataType<BigInteger> NUMBER = new NumberType();
	
	public static void initialize() {
		Registry.register(REGISTRY, Rho.id("empty"), EMPTY);
		Registry.register(REGISTRY, Rho.id("item_stack"), ITEM_STACK);
		Registry.register(REGISTRY, Rho.id("number"), NUMBER);
	}
}
