package repulica.cardstock.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import repulica.cardstock.client.screen.CardBinderScreen;
import repulica.cardstock.component.CardBinderInventory;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen {
	protected MixinHandledScreen(Text title) {
		super(title);
	}

	private static final ThreadLocal<Slot> SLOT_LOCAL = new ThreadLocal<>();

	@Inject(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z"), locals= LocalCapture.CAPTURE_FAILEXCEPTION)
	private void cacheSlot(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info, int x, int y, int slotX, int slotY, int slotIndex, Slot slot) {
		SLOT_LOCAL.set(slot);
	}

	@Redirect(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/screen/ingame/HandledScreen;fillGradient(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
	protected void hookSmallCardDraw(HandledScreen<?> caller, MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		if (caller instanceof CardBinderScreen && SLOT_LOCAL.get().inventory instanceof CardBinderInventory) {
			this.fillGradient(matrices, startX+5, startY+2, endX-5, startY+3, colorStart, colorEnd);
			this.fillGradient(matrices, startX+4, startY+3, endX-4, endY-3, colorStart, colorEnd);
			this.fillGradient(matrices, startX+5, endY-3, endX-5, endY-2, colorStart, colorEnd);
		} else {
			this.fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd);
		}
	}
}
