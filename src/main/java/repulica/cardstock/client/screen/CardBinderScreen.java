package repulica.cardstock.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.QuiltLoader;
import repulica.cardstock.CardStock;
import repulica.cardstock.screen.CardBinderScreenHandler;

import java.util.Calendar;

//todo: make a custom gui that looks better someday probably
public class CardBinderScreen extends HandledScreen<CardBinderScreenHandler> implements ScreenHandlerProvider<CardBinderScreenHandler> {
	private static final Identifier TEXTURE = new Identifier(CardStock.MODID, "textures/gui/container/card_binder.png");
	private static final Identifier APRILFOOLS = new Identifier(CardStock.MODID, "textures/gui/container/card_binder_aprilfools.png");
	public final boolean isSussy;
	private final int rows;

	public CardBinderScreen(CardBinderScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.passEvents = false;
		this.rows = handler.getRows();
		this.backgroundHeight = 114 + this.rows * 18;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) <= 2) {
			isSussy = true;
		} else {
			isSussy = QuiltLoader.getConfigDir().resolve("cardstock/sus.txt").toFile().exists();
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, isSussy ? APRILFOOLS : TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, 6 * 18 + 17);
		this.drawTexture(matrices, i, j + 6 * 18 + 17, 0, 126, this.backgroundWidth, 96);
	}
}
