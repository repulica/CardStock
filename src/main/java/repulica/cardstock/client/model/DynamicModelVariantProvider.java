package repulica.cardstock.client.model;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import repulica.cardstock.CardStock;

public class DynamicModelVariantProvider implements ModelVariantProvider {
	private static final ModelIdentifier CARD = new ModelIdentifier(new Identifier(CardStock.MODID, "card"), "inventory");
	private static final ModelIdentifier PACK = new ModelIdentifier(new Identifier(CardStock.MODID, "card_pack"), "inventory");

	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
		if (modelId.equals(CARD) || modelId.equals(PACK)) return new DynamicModel();
		return null;
	}
}
