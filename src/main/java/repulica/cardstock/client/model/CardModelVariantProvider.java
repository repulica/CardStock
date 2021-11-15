package repulica.cardstock.client.model;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import repulica.cardstock.CardStock;

public class CardModelVariantProvider implements ModelVariantProvider {
	private static final ModelIdentifier CARD = new ModelIdentifier(new Identifier(CardStock.MODID, "card"), "inventory");
	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
		if (modelId.equals(CARD)) return new CardModel();
		return null;
	}
}
