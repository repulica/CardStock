package repulica.cardstock.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import repulica.cardstock.CardStock;
import repulica.cardstock.client.model.CardModelVariantProvider;

import java.util.Collection;

public class CardStockClient implements ClientModInitializer {
	private static final int START = "models/item/".length();
	private static final int JSON_END = ".json".length();

	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> new CardModelVariantProvider());
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, consumer) -> {
			Collection<Identifier> cards = manager.findResources("models/item/card", string -> string.endsWith(".json"));
			for (Identifier id : cards) {
				consumer.accept(new ModelIdentifier(new Identifier(id.getNamespace(), id.getPath().substring(START, id.getPath().length() - JSON_END)), "inventory"));
			}
		});
		ColorProviderRegistry.ITEM.register((stack, index) -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null) return 0xFFFFFF;
			if (index == 1) {
				return MathHelper.hsvToRgb(Math.abs(player.getHeadYaw() % 360) / 360F, 0.25F, 1F);
			} else if (index == 2) {
				return MathHelper.hsvToRgb(Math.abs((player.getHeadYaw() + 90) % 360) / 360F, 0.25F, 1F);
			}
			return 0xFFFFFF;
		}, CardStock.CARD);
	}
}
