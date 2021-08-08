package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;

public class CorporeaTicketItem extends Item {
	public CorporeaTicketItem(Properties props) {
		super(props);
	}
	
	private static final String KEY = "solidified_corporea_request"; 
	
	public ItemStack produceForRequest(ICorporeaRequestMatcher request, int count) {
		ItemStack stack = new ItemStack(this);
		
		CompoundNBT tag = stack.getOrCreateTag();
		tag.put(KEY, new SolidifiedRequest(request, count).toTag());
		
		return stack;
	}
	
	public Optional<SolidifiedRequest> getRequest(ItemStack stack) {
		if(!stack.hasTag()) return Optional.empty();
		assert stack.getTag() != null;
		
		if(stack.getChildTag(KEY) == null) return Optional.empty();
		CompoundNBT requestNbt = stack.getChildTag(KEY);
		assert requestNbt != null;
		
		return SolidifiedRequest.tryFromTag(requestNbt);
	}
	
	public boolean hasRequest(ItemStack stack) {
		return getRequest(stack).isPresent();
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return getRequest(stack)
			.<ITextComponent>map(request -> new TranslationTextComponent("item.incorporeal.corporea_ticket.has", request.toText()))
			.orElseGet(() -> super.getDisplayName(stack));
	}
}
