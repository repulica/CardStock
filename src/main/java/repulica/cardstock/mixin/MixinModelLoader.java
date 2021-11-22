package repulica.cardstock.mixin;

import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import repulica.cardstock.client.CardStockClient;

@Mixin(ModelLoader.class)
public class MixinModelLoader {
	@Shadow @Nullable private SpriteAtlasManager spriteAtlasManager;

	@Inject(method="loadModelFromJson", at=@At("HEAD"), cancellable=true)
	private void hookLoadingModel(Identifier id, CallbackInfoReturnable<JsonUnbakedModel> info) {
		if (id.equals(CardStockClient.CARD_MARKER_ID)) {
			info.setReturnValue(CardStockClient.CARD_MARKER);
		}
	}

	@Inject(
			method="bake",
			at=@At(value="INVOKE", target="Lnet/minecraft/client/render/model/json/JsonUnbakedModel;getRootModel()Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"),
			cancellable=true,
			locals= LocalCapture.CAPTURE_FAILEXCEPTION
	)
	private void hookBakingModel(Identifier id, ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> info,
								 Triple<Identifier, AffineTransformation, Boolean> triple, UnbakedModel unbakedModel,
								 JsonUnbakedModel jsonUnbakedModel) {
		if (jsonUnbakedModel.getRootModel() == CardStockClient.CARD_MARKER) {
			info.setReturnValue(CardStockClient.CARD_MODEL_GENERATOR.create(this.spriteAtlasManager::getSprite, jsonUnbakedModel).bake((ModelLoader) (Object) this, jsonUnbakedModel, spriteAtlasManager::getSprite, settings, id, false));
		}
	}
}
