package repulica.cardstock.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import repulica.cardstock.client.screen.CardBinderScreen;
import repulica.cardstock.component.CardBinderInventory;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen {
	protected MixinHandledScreen(Text title) {
		super(title);
	}

	private static final int FILL_COLOR = 0x80FFFFFF;
	private static final ThreadLocal<Screen> CALLING_SCREEN = new ThreadLocal<>();
	private static final ThreadLocal<Slot> SLOT_LOCAL = new ThreadLocal<>();

	@Inject(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z"), locals= LocalCapture.CAPTURE_FAILEXCEPTION)
	private void cacheSlot(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, int x, int y, MatrixStack matrixStack, int focusedSlot, Slot slot) {
		CALLING_SCREEN.set(this);
		SLOT_LOCAL.set(slot);
	}

	@Inject(method = "drawSlotHighlight", at = @At("HEAD"), cancellable = true)
	private static void hookSmallCardDraw(MatrixStack matrices, int x, int y, int z, CallbackInfo info) {
		if (CALLING_SCREEN.get() instanceof CardBinderScreen sc && SLOT_LOCAL.get().inventory instanceof CardBinderInventory) {
			RenderSystem.disableDepthTest();
			RenderSystem.colorMask(true, true, true, false);
			fillGradient(matrices, x+3, y, x+13, y+1, FILL_COLOR, FILL_COLOR, z);
			fillGradient(matrices, x+2, y+1, x+14, y+2, FILL_COLOR, FILL_COLOR, z);
			if (!sc.isSussy) {
				fillGradient(matrices, x+1, y+2, x+15, y+14, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+2, y+14, x+14, y+15, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+3, y+15, x+13, y+16, FILL_COLOR, FILL_COLOR, z);
			} else {
				//highlight to fit among us texture
				fillGradient(matrices, x+1, y+2, x+15, y+13, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+1, y+13, x+6, y+14, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+10, y+13, x+15, y+14, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+2, y+14, x+5, y+15, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+11, y+14, x+14, y+15, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+3, y+15, x+5, y+16, FILL_COLOR, FILL_COLOR, z);
				fillGradient(matrices, x+11, y+15, x+13, y+16, FILL_COLOR, FILL_COLOR, z);
			}
			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.enableDepthTest();
			info.cancel();
		}
	}
}
