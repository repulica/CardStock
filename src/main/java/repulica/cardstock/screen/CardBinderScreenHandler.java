package repulica.cardstock.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import repulica.cardstock.CardStock;
import repulica.cardstock.component.CardBinderInventory;

public class CardBinderScreenHandler extends GenericContainerScreenHandler {
	private ItemStack stack;

	public CardBinderScreenHandler(ItemStack stack, ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
		this(type, syncId, playerInventory, inventory, rows);
		this.stack = stack;
	}

	public CardBinderScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
		super(type, syncId, playerInventory, inventory, rows);
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		if (slot.inventory instanceof CardBinderInventory && stack.getItem() != CardStock.CARD) return false;
		return super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		if (slotIndex != -999) {
			ItemStack stack = getSlot(slotIndex).getStack();
			if (stack == this.stack) return;
		}
		super.onSlotClick(slotIndex, button, actionType, player);
	}
}
