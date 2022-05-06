package repulica.cardstock.mixin;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import repulica.cardstock.CardStock;

@Mixin(ItemModels.class)
public abstract class MixinItemModels {
	@Shadow public abstract BakedModelManager getModelManager();
	private static final ModelIdentifier MISSINGNO_CARD = new ModelIdentifier(new Identifier(CardStock.MODID, "card/missingno/missingno"), "inventory");
	private static final ModelIdentifier MISSINGNO_PACK = new ModelIdentifier(new Identifier(CardStock.MODID, "pack/missingno"), "inventory");

	@Inject(method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
	private void injectItemModel(ItemStack stack, CallbackInfoReturnable<BakedModel> info) {
		if (stack.getItem() == CardStock.CARD) {
			if (stack.hasNbt() && stack.getNbt().contains("Card", NbtElement.STRING_TYPE)) {
				Identifier cardId = new Identifier(stack.getNbt().getString("Card"));
				ModelIdentifier modelId = new ModelIdentifier(new Identifier(cardId.getNamespace(), "card/" + cardId.getPath()), "inventory");
				info.setReturnValue(getDynModel(modelId, MISSINGNO_CARD));
			} else {
				info.setReturnValue(this.getModelManager().getModel(MISSINGNO_CARD));
			}
		} else if (stack.getItem() == CardStock.CARD_PACK) {
			if (stack.hasNbt() && stack.getNbt().contains("Pack", NbtElement.STRING_TYPE)) {
				Identifier packId = new Identifier(stack.getNbt().getString("Pack"));
				ModelIdentifier modelId = new ModelIdentifier(new Identifier(packId.getNamespace(), "pack/" + packId.getPath().substring("packs/".length())), "inventory");
				info.setReturnValue(getDynModel(modelId, MISSINGNO_PACK));
			} else {
				info.setReturnValue(this.getModelManager().getModel(MISSINGNO_PACK));
			}
		}
	}

	private BakedModel getDynModel(ModelIdentifier modelId, ModelIdentifier fallback) {
		BakedModel model = this.getModelManager().getModel(modelId);
		if (model == this.getModelManager().getMissingModel()) {
			return this.getModelManager().getModel(fallback);
		} else {
			return model;
		}
	}
}
