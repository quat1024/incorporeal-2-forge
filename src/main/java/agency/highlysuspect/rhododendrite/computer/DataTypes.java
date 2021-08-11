package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.Rho;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Lifecycle;
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
	public static final DataType<SolidifiedRequest> SOLIDIFIED_REQUEST = new SolidifiedRequestType();
	public static final DataType<BigInteger> BIG_INTEGER = new BigIntegerType();
	
	public static void initialize() {
		Registry.register(REGISTRY, Rho.id("empty"), EMPTY);
		Registry.register(REGISTRY, Rho.id("solidified_request"), SOLIDIFIED_REQUEST);
		Registry.register(REGISTRY, Rho.id("big_integer"), BIG_INTEGER);
	}
}
