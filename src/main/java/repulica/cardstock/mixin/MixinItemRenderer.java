package repulica.cardstock.mixin;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import repulica.cardstock.CardStock;
import repulica.cardstock.client.CardColorProvider;
import repulica.cardstock.client.render.CardStockRenderLayers;

//renderItemHead and renderItemTail courtesy of unascribed in yttr
//used with permission and under compatible license
//same with renderGuiItemModel from smithy
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);

	@Inject(at=@At("HEAD"), method="renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V")
	public void renderItemHead(LivingEntity entity, ItemStack stack, ModelTransformation.Mode mode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vcp, World w, int light, int overlay, int seed, CallbackInfo ci) {
		if (entity != null && stack.getItem() == CardStock.CARD) {
			if (mode.isFirstPerson()) return;
			CardColorProvider.holderYaw = entity.bodyYaw;
			CardColorProvider.holderYawValid = true;
		}
	}

	@Inject(at=@At("RETURN"), method="renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V")
	public void renderItemTail(LivingEntity entity, ItemStack stack, ModelTransformation.Mode mode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vcp, World w, int light, int overlay, int seed, CallbackInfo ci) {
		if (entity != null && stack.getItem() == CardStock.CARD) {
			CardColorProvider.holderYawValid = false;
		}
	}

//	@Inject(cancellable=true,
//			at=@At(value="INVOKE",target="Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"),
//			method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V")
//	private void renderFoil(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo info) {
//		if (stack.getItem() == CardStock.CARD) {
//			Card card = CardManager.INSTANCE.getCard(stack);
//			if (card.getRarity() == 5) {
//				RenderLayer renderLayer = CardStockRenderLayers.CARD_GLITTER;
//				VertexConsumer consumer = CardStockRenderLayers.CONSUMER.getBuffer(renderLayer);
//				this.renderBakedItemModel(model, stack, light, overlay, matrices, consumer);
//				matrices.pop();
//				info.cancel();
//			}
//		}
//	}

	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/render/VertexConsumerProvider$Immediate.draw()V", shift=At.Shift.AFTER), method="renderGuiItemModel")
	protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
		CardStockRenderLayers.CONSUMER.draw(CardStockRenderLayers.CARD_GLITTER);
	}

}
