package repulica.cardstock.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import repulica.cardstock.CardStock;
import repulica.cardstock.component.CardStockComponents;
import repulica.cardstock.screen.CardBinderScreenHandler;

public class CardBinderItem extends Item implements DyeableItem {
	public CardBinderItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient) {
			ItemStack stack = user.getStackInHand(hand);
			user.openHandledScreen(new BinderFactory(stack));
			return TypedActionResult.success(stack);
		}
		return super.use(world, user, hand);
	}

	public static class BinderFactory implements NamedScreenHandlerFactory {
		private final ItemStack stack;

		public BinderFactory(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public Text getDisplayName() {
			return new TranslatableText("item.cardstock.card_binder");
		}

		@Nullable
		@Override
		public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
			return new CardBinderScreenHandler(stack, CardStock.CARD_BINDER_HANDLER, syncId, inv, CardStockComponents.CARD_BINDER.get(stack).getInv(), 6);
		}
	}
}
