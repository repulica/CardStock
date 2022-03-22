package repulica.cardstock.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import repulica.cardstock.CardStock;

import java.util.Calendar;

//todo: make a custom gui that looks better someday probably
public class CardBinderScreen extends GenericContainerScreen {
	private static final Identifier TEXTURE = new Identifier(CardStock.MODID, "textures/gui/container/card_binder.png");
	private static final Identifier APRILFOOLS = new Identifier(CardStock.MODID, "textures/gui/container/card_binder_aprilfools.png");
	private boolean isAprilFools = false;

	public CardBinderScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) <= 2) {
			isAprilFools = true;
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(isAprilFools? APRILFOOLS : TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, 6 * 18 + 17);
		this.drawTexture(matrices, i, j + 6 * 18 + 17, 0, 126, this.backgroundWidth, 96);
	}
}
