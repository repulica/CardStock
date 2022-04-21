package repulica.cardstock.component;

import dev.onyxstudios.cca.api.v3.item.CcaNbtType;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;

public class ItemCardBinderComponent extends ItemComponent implements CardBinderComponent {

	public ItemCardBinderComponent(ItemStack stack) {
		super(stack, CardStockComponents.CARD_BINDER);
	}

	@Override
	public CardBinderInventory getInv() {
		CardBinderInventory ret = new CardBinderInventory();
		ret.addListener(sender -> this.putList("Items", ret.toNbtList()));
		if (this.hasTag("Items", CcaNbtType.LIST)) {
			ret.readNbtList(this.getList("Items", NbtElement.COMPOUND_TYPE));
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemCardBinderComponent) {
			return getInv().equals(((ItemCardBinderComponent) obj).getInv());
		}
		return false;
	}
}
