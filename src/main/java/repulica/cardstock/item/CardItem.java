package repulica.cardstock.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import repulica.cardstock.api.Card;
import repulica.cardstock.api.CardManager;
import repulica.cardstock.client.tooltip.CardTooltipComponent;

import java.util.List;
import java.util.Optional;

public class CardItem extends Item {

	public CardItem(Settings settings) {
		super(settings);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return CardManager.INSTANCE.getCard(stack).getItemRarity();
	}

//	@Override
//	public boolean hasGlint(ItemStack stack) {
//		return CardManager.INSTANCE.getCard(stack).getRarity() == 5;
//	}

	@Override
	public Text getName(ItemStack stack) {
		if (stack.hasNbt() && stack.getOrCreateNbt().contains("Card", NbtElement.STRING_TYPE)) {
			return new TranslatableText("card." + stack.getOrCreateNbt().getString("Card")
					.replace(':', '.')
					.replace('/', '.')
			);
		}
		return super.getName(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(new LiteralText(""));
		Card card = CardManager.INSTANCE.getCard(stack);
		if (!card.toString().equals("")) tooltip.add(card.info());
		if (Screen.hasShiftDown()) {
			for (Text line : card.lore()) {
				tooltip.add(new LiteralText("  ").append(line));
			}
			if (card.artist() != null) {
				tooltip.add(new TranslatableText("text.cardstock.artist", card.artist(), card.date()).formatted(Formatting.GRAY));
			}
		} else {
			tooltip.add(new TranslatableText("text.cardstock.more").formatted(Formatting.GRAY));
		}
		if (context.isAdvanced()) {
			tooltip.add(new TranslatableText("text.cardstock.source").formatted(Formatting.BLUE, Formatting.ITALIC));
		}
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return CardTooltipComponent.of(stack).or(() -> super.getTooltipData(stack));
	}
}
