package repulica.cardstock.component;

import dev.onyxstudios.cca.api.v3.item.CcaNbtType;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;

public class ItemCardBinderComponent extends ItemComponent implements CardBinderComponent {

	public ItemCardBinderComponent(ItemStack stack) {
		super(stack, CardStockComponents.CARD_BINDER);
	}

	@Override
	public CardBinderInventory getInv() {
		CardBinderInventory ret = new CardBinderInventory();
		ret.addListener(sender -> this.putList("Items", ret.toNbtList()));
		if (this.hasTag("Items", CcaNbtType.LIST)) {
			ret.readNbtList(this.getList("Items", NbtType.COMPOUND));
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
