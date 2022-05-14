package repulica.cardstock.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import repulica.cardstock.CardStock;

import java.util.Map;

public class CardStockRenderLayers extends RenderLayer {
	private static VertexConsumerProvider.Immediate CONSUMER;
	protected static final Identifier CARD_GLITTER_TEXTURE = new Identifier(CardStock.MODID, "textures/misc/card_glitter.png");
	protected static final Texturing CARD_GLITTER_TEXTURING = new Texturing("card_glitter_texturing", () -> setupCardGlitterTexturing(8.0F), RenderSystem::resetTextureMatrix);
	public static final RenderLayer CARD_GLITTER = RenderLayer.of(
			"card_glitter",
			VertexFormats.POSITION_TEXTURE,
			VertexFormat.DrawMode.QUADS,
			256, true, true,
			RenderLayer.MultiPhaseParameters.builder()
					.shader(GLINT_SHADER)
					.texture(new RenderPhase.Texture(CARD_GLITTER_TEXTURE, true, false))
					.writeMaskState(COLOR_MASK)
					.cull(DISABLE_CULLING)
					.depthTest(EQUAL_DEPTH_TEST)
					.transparency(GLINT_TRANSPARENCY)
					.texturing(CARD_GLITTER_TEXTURING)
					.build(false));

	public CardStockRenderLayers(String string, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
		super(string, vertexFormat, drawMode, i, bl, bl2, runnable, runnable2);
	}

	private static void setupCardGlitterTexturing(float scale) {
		//todo: tweak later probably
		long l = Util.getMeasuringTimeMs() * 8L;
		float f = (float)(l % 110000L) / 110000.0F;
		float g = (float)(l % 30000L) / 30000.0F;
		Matrix4f matrix4f = Matrix4f.translate(-f, g, 0.0F);
//		Matrix4f matrix4f = Matrix4f.translate(0.0F, 0.0F, 0.0F);
		matrix4f.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(10.0F));
		matrix4f.multiply(Matrix4f.scale(scale, scale, scale));
		RenderSystem.setTextureMatrix(matrix4f);
	}

	public static VertexConsumerProvider.Immediate getConsumer() {
		return CONSUMER;
	}

	public static void init() {
		CONSUMER = VertexConsumerProvider.immediate(Map.of(CARD_GLITTER, new BufferBuilder(256)), new BufferBuilder(256));
	}
}
