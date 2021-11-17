package repulica.cardstock.component;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import repulica.cardstock.CardStock;

public class CardBinderInventory extends SimpleInventory {
	public CardBinderInventory() {
		super(54);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return stack.getItem() == CardStock.CARD;
	}
}
