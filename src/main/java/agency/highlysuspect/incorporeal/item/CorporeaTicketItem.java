package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;

import net.minecraft.item.Item.Properties;

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
		
		CompoundNBT tag = stack.getOrCreateTag();
		tag.put(KEY, new SolidifiedRequest(request, count).toTag());
		
		return stack;
	}
	
	public Optional<SolidifiedRequest> getRequest(ItemStack stack) {
		if(!stack.hasTag()) return Optional.empty();
		assert stack.getTag() != null;
		
		if(stack.getTagElement(KEY) == null) return Optional.empty();
		CompoundNBT requestNbt = stack.getTagElement(KEY);
		assert requestNbt != null;
		
		return SolidifiedRequest.tryFromTag(requestNbt);
	}
	
	public boolean hasRequest(ItemStack stack) {
		return getRequest(stack).isPresent();
	}
	
	@Override
	public ITextComponent getName(ItemStack stack) {
		return getRequest(stack)
			.<ITextComponent>map(request -> new TranslationTextComponent("item.incorporeal.corporea_ticket.has", request.toText()))
			.orElseGet(() -> super.getName(stack));
	}
}
