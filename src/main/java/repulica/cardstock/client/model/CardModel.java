package repulica.cardstock.client.model;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import repulica.cardstock.CardStock;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CardModel implements BakedModel, FabricBakedModel, UnbakedModel {
	private static final BakedModelManager MANAGER = MinecraftClient.getInstance().getBakedModelManager();
	private static final Identifier ITEM_GENERATED = new Identifier("minecraft:item/generated");
	private static final ModelIdentifier MISSINGNO = new ModelIdentifier(new Identifier(CardStock.MODID, "card/missingno/missingno"), "inventory");
	private ModelTransformation transformation;

	public CardModel() {
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		if (stack.hasTag() && stack.getTag().contains("Card", NbtType.STRING)) {
			Identifier cardId = new Identifier(stack.getTag().getString("Card"));
			ModelIdentifier modelId = new ModelIdentifier(new Identifier(cardId.getNamespace(), "card/" + cardId.getPath()), "inventory");
			((FabricBakedModel) MANAGER.getModel(modelId)).emitItemQuads(stack, randomSupplier, context);
		} else {
			((FabricBakedModel) MANAGER.getModel(MISSINGNO)).emitItemQuads(stack, randomSupplier, context);
		}
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) { }

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return null;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean hasDepth() {
		return false;
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getSprite() {
		return null;
	}

	@Override
	public ModelTransformation getTransformation() {
		return transformation;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.singleton(MISSINGNO);
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
		return Collections.emptyList();
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		transformation = ((JsonUnbakedModel) loader.getOrLoadModel(ITEM_GENERATED)).getTransformations();
		return this;
	}
}
