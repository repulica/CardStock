package repulica.cardstock.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import repulica.cardstock.client.CardStockClient;
import repulica.cardstock.client.model.CardModelGenerator;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public abstract class MixinJsonUnbakedModel {
	@Shadow public abstract JsonUnbakedModel getRootModel();

	@Shadow public abstract SpriteIdentifier resolveSprite(String spriteName);

	@Inject(method="getTextureDependencies", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/model/json/JsonUnbakedModel;getRootModel()Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"), locals= LocalCapture.CAPTURE_FAILEXCEPTION)
	private void getCardModelDeps(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences, CallbackInfoReturnable<Collection<SpriteIdentifier>> info, Set<SpriteIdentifier> dependencies) {
		if (this.getRootModel() == CardStockClient.CARD_MARKER) {
			CardModelGenerator.LAYERS.forEach((string) -> dependencies.add(this.resolveSprite(string)));
		}
	}
}
