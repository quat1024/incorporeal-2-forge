package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;

import net.minecraft.world.item.Item.Properties;

public class CorporeaTicketItem extends Item {
	public CorporeaTicketItem(Properties props) {
		super(props);
	}
	
	private static final String KEY = "solidified_corporea_request"; 
	
	public ItemStack produce(SolidifiedRequest request) {
		return produceForRequest(request.matcher, request.count);
	}
	
	public ItemStack produceForRequest(ICorporeaRequestMatcher request, int count) {
		ItemStack stack = new ItemStack(this);
		
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(KEY, new SolidifiedRequest(request, count).toTag());
		
		return stack;
	}
	
	public Optional<SolidifiedRequest> getRequest(ItemStack stack) {
		if(!stack.hasTag()) return Optional.empty();
		assert stack.getTag() != null;
		
		if(stack.getTagElement(KEY) == null) return Optional.empty();
		CompoundTag requestNbt = stack.getTagElement(KEY);
		assert requestNbt != null;
		
		return SolidifiedRequest.tryFromTag(requestNbt);
	}
	
	public boolean hasRequest(ItemStack stack) {
		return getRequest(stack).isPresent();
	}
	
	@Override
	public Component getName(ItemStack stack) {
		return getRequest(stack)
			.<Component>map(request -> new TranslatableComponent("item.incorporeal.corporea_ticket.has", request.toText()))
			.orElseGet(() -> super.getName(stack));
	}
}
