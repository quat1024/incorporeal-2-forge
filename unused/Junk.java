package agency.highlysuspect.rhododendrite;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Junk {
	public static <K, V> ListNBT writeMap(Map<K, V> map, BiConsumer<K, CompoundNBT> writeKey, BiConsumer<V, CompoundNBT> writeValue) {
		ListNBT list = new ListNBT();
		map.forEach((k, v) -> {
			CompoundNBT entry = new CompoundNBT();
			writeKey.accept(k, entry);
			writeValue.accept(v, entry);
			list.add(entry);
		});
		return list;
	}
	
	public static <K, V, M extends Map<K, V>> M readMap(Supplier<M> constructor, ListNBT list, Function<CompoundNBT, K> keyReader, Function<CompoundNBT, V> valueReader) {
		M map = constructor.get();
		
		list.forEach(e -> {
			if(e instanceof CompoundNBT) {
				CompoundNBT nbt = (CompoundNBT) e;
				map.put(keyReader.apply(nbt), valueReader.apply(nbt));
			}
		});
		
		return map;
	} 
}
