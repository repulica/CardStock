package repulica.cardstock.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import repulica.cardstock.CardStock;
import repulica.cardstock.client.model.CardModelGenerator;
import repulica.cardstock.client.model.CardModelVariantProvider;

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
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> new CardModelVariantProvider());
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, consumer) -> {
			Collection<Identifier> cards = manager.findResources("models/item/card", string -> string.endsWith(".json"));
			for (Identifier id : cards) {
				consumer.accept(new ModelIdentifier(new Identifier(id.getNamespace(), id.getPath().substring(START, id.getPath().length() - JSON_END)), "inventory"));
			}
		});
		ColorProviderRegistry.ITEM.register(new CardColorProvider(), CardStock.CARD);
	}
}
