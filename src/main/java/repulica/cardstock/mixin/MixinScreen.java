package repulica.cardstock.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import repulica.cardstock.CardStock;
import repulica.cardstock.data.CardManager;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement {
	@Shadow @Nullable protected MinecraftClient client;
	private static final Identifier STAR_ID = new Identifier(CardStock.MODID, "textures/gui/star.png");
	private static final Identifier EMPTY_STAR_ID = new Identifier(CardStock.MODID, "textures/gui/empty_star.png");
	private static final ThreadLocal<ItemStack> hoveredStack = ThreadLocal.withInitial(() -> ItemStack.EMPTY);

	@Inject(method="renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at=@At("HEAD"))
	private void cacheHoveredStack(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo info) {
		hoveredStack.set(stack);
	}

	@ModifyConstant(method="renderOrderedTooltip", constant=@Constant(intValue=0, ordinal=0))
	private int tweakTooltipWidth(int original) {
		if (hoveredStack.get().getItem() == CardStock.CARD) {
			return original + 80;
		}
		return original;
	}

	@ModifyConstant(method="renderOrderedTooltip", constant=@Constant(intValue=8))
	private int tweakTooltipHeight(int original) {
		if (hoveredStack.get().getItem() == CardStock.CARD) {
			return original + 11;
		}
		return original;
	}

	@Inject(method="renderOrderedTooltip", at=@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;pop()V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void renderRarityValue(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y, CallbackInfo info, int width, int drawX, int drawY) {
		if (hoveredStack.get().getItem() == CardStock.CARD) {
			ItemStack stack = hoveredStack.get();
			if (CardManager.INSTANCE.getCard(stack).getRarity() != 0) {
				this.client.getTextureManager().bindTexture(STAR_ID);
				for (int i = 0; i < CardManager.INSTANCE.getCard(stack).getRarity(); i++) {
					DrawableHelper.drawTexture(matrices, drawX + i * 10, drawY, 400, 0, 0, 9, 9, 9, 9);
				}
				this.client.getTextureManager().bindTexture(EMPTY_STAR_ID);
				for (int i = CardManager.INSTANCE.getCard(stack).getRarity(); i < 5; i++) {
					DrawableHelper.drawTexture(matrices, drawX + i * 10, drawY, 400, 0, 0, 9, 9, 9, 9);
				}
				this.client.getTextureManager().bindTexture(CardManager.INSTANCE.getSet(stack).getEmblem());
				DrawableHelper.drawTexture(matrices, drawX + 70, drawY, 400, 0, 0, 9, 9, 9, 9);
			}
		}
		hoveredStack.set(ItemStack.EMPTY);
	}
}
