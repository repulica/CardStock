package repulica.cardstock.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import repulica.cardstock.CardStock;

public class CardBinderInventory extends SimpleInventory {
	public CardBinderInventory() {
		super(54);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return stack.getItem() == CardStock.CARD;
	}

	@Override
	public void readNbtList(NbtList nbtList) {
		for(int i = 0; i < nbtList.size(); ++i) {
			ItemStack itemStack = ItemStack.fromNbt(nbtList.getCompound(i));
			if (!itemStack.isEmpty()) {
				this.setStack(i, itemStack);
			}
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		super.onClose(player);
		markDirty();
	}

	@Override
	public NbtList toNbtList() {
		NbtList nbtList = new NbtList();

		for(int i = 0; i < this.size(); ++i) {
			nbtList.add(this.getStack(i).writeNbt(new NbtCompound()));
		}

		return nbtList;
	}
}
