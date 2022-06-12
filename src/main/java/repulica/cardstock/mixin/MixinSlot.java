package repulica.cardstock.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import repulica.cardstock.component.CardBinderInventory;

//stupid bad stupid garbage because slots cannot even comprehend respecting their underlying inventory on whether items should be inserted
@Mixin(Slot.class)
public class MixinSlot {
	@Shadow @Final public Inventory inventory;

	@Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
	private void hookInsertionRule(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if (inventory instanceof CardBinderInventory) {
			info.setReturnValue(((CardBinderInventory) inventory).canInsert(stack));
		}
	}
}
