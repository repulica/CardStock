package repulica.cardstock.client;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.DyeableItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import repulica.cardstock.CardStock;
import repulica.cardstock.api.CardManager;
import repulica.cardstock.api.Holofoil;
import repulica.cardstock.api.HolofoilType;
import repulica.cardstock.api.SimpleHolofoilType;
import repulica.cardstock.client.model.CardModelGenerator;
import repulica.cardstock.client.render.CardStockRenderLayers;
import repulica.cardstock.client.screen.CardBinderScreen;
import repulica.cardstock.data.CardManagerImpl;
import repulica.cardstock.holofoil.RainbowHolofoil;

import java.util.Collection;

public class CardStockClient implements ClientModInitializer {
	private static final int START = "models/item/".length();
	private static final int JSON_END = ".json".length();

	public static final Identifier CARD_MARKER_ID = new Identifier(CardStock.MODID, "builtin/card");
	public static final JsonUnbakedModel CARD_MARKER = Util.make(
			JsonUnbakedModel.deserialize("{\"gui_light\": \"front\"}"),
			model -> model.id = "cardstock card marker"
	);
	public static final CardModelGenerator CARD_MODEL_GENERATOR = new CardModelGenerator();

	@Override
	public void onInitializeClient(ModContainer container) {
		HandledScreens.register(CardStock.CARD_BINDER_HANDLER, CardBinderScreen::new);
		CardStockRenderLayers.init();
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, consumer) -> {
			Collection<Identifier> cards = manager.findResources("models/item/card", string -> string.endsWith(".json"));
			for (Identifier id : cards) {
				consumer.accept(new ModelIdentifier(new Identifier(id.getNamespace(), id.getPath().substring(START, id.getPath().length() - JSON_END)), "inventory"));
			}
			Collection<Identifier> packs = manager.findResources("models/item/pack", string -> string.endsWith(".json"));
			for (Identifier id : packs) {
				consumer.accept(new ModelIdentifier(new Identifier(id.getNamespace(), id.getPath().substring(START, id.getPath().length() - JSON_END)), "inventory"));
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(CardStock.CARD_SYNC, (server, handler, buf, responseSender) -> CardManagerImpl.INSTANCE.recievePacket(buf));
		ColorProviderRegistry.ITEM.register(new CardColorProvider(), CardStock.CARD);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0? ((DyeableItem) stack.getItem()).getColor(stack) : 0xFFFFFF, CardStock.CARD_BINDER);
	}
}
