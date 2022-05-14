package repulica.cardstock.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import repulica.cardstock.client.render.CardStockRenderLayers;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	@Inject(method="renderWorld", at = @At("TAIL"))
	private void injectRender(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo info) {
		CardStockRenderLayers.getConsumer().draw(CardStockRenderLayers.CARD_GLITTER);
	}
}
