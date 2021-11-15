package repulica.cardstock.item;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import repulica.cardstock.data.Card;
import repulica.cardstock.data.CardManager;

import java.util.List;

public class CardItem extends Item {

	public CardItem(Settings settings) {
		super(settings);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return CardManager.INSTANCE.getCard(stack).getItemRarity();
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return CardManager.INSTANCE.getCard(stack).getRarity() == 5;
	}

	@Override
	public Text getName(ItemStack stack) {
		if (stack.hasTag() && stack.getOrCreateTag().contains("Card", NbtType.STRING)) {
			return new TranslatableText("card." + stack.getOrCreateTag().getString("Card")
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
		if (!card.toString().equals("")) tooltip.add(card.getInfo());
		if (Screen.hasShiftDown()) {
			for (Text line : card.getLore()) {
				tooltip.add(new LiteralText("  ").append(line));
			}
			if (card.getArtist() != null) {
				tooltip.add(new TranslatableText("text.cardstock.artist", card.getArtist(), card.getDate()).formatted(Formatting.GRAY));
			}
		} else {
			tooltip.add(new TranslatableText("text.cardstock.more").formatted(Formatting.GRAY));
		}
		if (context.isAdvanced()) {
			tooltip.add(new TranslatableText("text.cardstock.source").formatted(Formatting.BLUE, Formatting.ITALIC));
		}
	}
}
