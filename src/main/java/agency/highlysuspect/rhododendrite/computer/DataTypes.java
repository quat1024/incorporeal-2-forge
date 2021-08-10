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
	//the intention is to let other mods register data types as wlel,  but with the weird forge multithreaded shit i don't know the right way to do that
	//lmk
	//also yeah its a vanilla registry, sue me
	public static final RegistryKey<Registry<DataType<?>>> DATATYPES_KEY = RegistryKey.getOrCreateRootKey(Rho.id("datatypes"));
	public static final Registry<DataType<?>> REGISTRY = new SimpleRegistry<>(DATATYPES_KEY, Lifecycle.stable());
	
	public static final DataType<Unit> EMPTY = new DataType.Empty();
	public static final DataType<ItemStack> ITEM_STACK = new ItemStackType();
	public static final DataType<BigInteger> NUMBER = new NumberType();
	
	public static void initialize() {
		Registry.register(REGISTRY, Rho.id("empty"), EMPTY);
		Registry.register(REGISTRY, Rho.id("item_stack"), ITEM_STACK);
		Registry.register(REGISTRY, Rho.id("number"), NUMBER);
	}
}
