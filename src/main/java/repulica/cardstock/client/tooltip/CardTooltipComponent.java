package repulica.cardstock.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;
import repulica.cardstock.CardStock;
import repulica.cardstock.api.CardManager;

import java.util.Optional;

public class CardTooltipComponent implements TooltipComponent, ConvertibleTooltipData {
	private static final Identifier STAR_ID = new Identifier(CardStock.MODID, "textures/gui/star.png");
	private static final Identifier EMPTY_STAR_ID = new Identifier(CardStock.MODID, "textures/gui/empty_star.png");
	private final ItemStack stack;

	public CardTooltipComponent(ItemStack stack) {
		this.stack = stack;
	}

	public static Optional<TooltipData> of(ItemStack stack) {
		return Optional.of(new CardTooltipComponent(stack));
	}

	@Override
	public int getHeight() {
		return 11;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 80;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		if (CardManager.INSTANCE.getCard(stack).getRarity() != 0) {
			RenderSystem.setShaderTexture(0, STAR_ID);
			for (int i = 0; i < CardManager.INSTANCE.getCard(stack).getRarity(); i++) {
				DrawableHelper.drawTexture(matrices, x + i * 10, y, z, 0, 0, 9, 9, 9, 9);
			}
			RenderSystem.setShaderTexture(0, EMPTY_STAR_ID);
			for (int i = CardManager.INSTANCE.getCard(stack).getRarity(); i < 5; i++) {
				DrawableHelper.drawTexture(matrices, x + i * 10, y, z, 0, 0, 9, 9, 9, 9);
			}
			RenderSystem.setShaderTexture(0, CardManager.INSTANCE.getSet(stack).getEmblem());
			DrawableHelper.drawTexture(matrices, x + 70, y, z, 0, 0, 9, 9, 9, 9);
		}
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}
}
