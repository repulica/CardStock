package repulica.cardstock.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

//alternate version of ItemModelGenerator that makes them half as thick
//todo: fix (doesnt work for some reason no idea why)
public class CardModelGenerator {
	public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

	public JsonUnbakedModel create(Function<SpriteIdentifier, Sprite> textureGetter, JsonUnbakedModel blockModel) {
		Map<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
		List<ModelElement> list = Lists.newArrayList();

		for(int i = 0; i < LAYERS.size(); i++) {
			String string = LAYERS.get(i);
			if (!blockModel.textureExists(string)) {
				break;
			}

			SpriteIdentifier spriteIdentifier = blockModel.resolveSprite(string);
			map.put(string, Either.left(spriteIdentifier));
			Sprite sprite = textureGetter.apply(spriteIdentifier);
			list.addAll(this.addLayerElements(i, string, sprite));
		}

		map.put("particle", blockModel.textureExists("particle") ? Either.left(blockModel.resolveSprite("particle")) : map.get("layer0"));
		JsonUnbakedModel model = new JsonUnbakedModel(null, list, map, false, blockModel.getGuiLight(), blockModel.getTransformations(), blockModel.getOverrides());
		model.id = blockModel.id;
		return model;
	}

	private List<ModelElement> addLayerElements(int layer, String key, Sprite sprite) {
		Map<Direction, ModelElementFace> map = Maps.newHashMap();
		map.put(Direction.SOUTH, new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
		map.put(Direction.NORTH, new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
		List<ModelElement> list = Lists.newArrayList();
		list.add(new ModelElement(new Vec3f(0.0F, 0.0F, 7.75F), new Vec3f(16.0F, 16.0F, 8.25F), map, null, true));
		list.addAll(this.addSubComponents(sprite, key, layer));
		return list;
	}

	private List<ModelElement> addSubComponents(Sprite sprite, String key, int layer) {
		float width = (float)sprite.getWidth();
		float height = (float)sprite.getHeight();
		List<ModelElement> list = Lists.newArrayList();

		for (Frame frame : getFrames(sprite)) {
			float h = 0.0F;
			float i = 0.0F;
			float j = 0.0F;
			float k = 0.0F;
			float l = 0.0F;
			float m = 0.0F;
			float n = 0.0F;
			float o = 0.0F;
			float p = 16.0F / width;
			float q = 16.0F / height;
			float min = (float)frame.getMin();
			float max = (float)frame.getMax();
			float level = (float)frame.getLevel();
			Side side = frame.getSide();
			switch(side) {
				case UP:
					l = min;
					h = min;
					j = m = max + 1.0F;
					n = level;
					i = level;
					k = level;
					o = level + 1.0F;
					break;
				case DOWN:
					n = level;
					o = level + 1.0F;
					l = min;
					h = min;
					j = m = max + 1.0F;
					i = level + 1.0F;
					k = level + 1.0F;
					break;
				case LEFT:
					l = level;
					h = level;
					j = level;
					m = level + 1.0F;
					o = min;
					i = min;
					k = n = max + 1.0F;
					break;
				case RIGHT:
					l = level;
					m = level + 1.0F;
					h = level + 1.0F;
					j = level + 1.0F;
					o = min;
					i = min;
					k = n = max + 1.0F;
			}

			h *= p;
			j *= p;
			i *= q;
			k *= q;
			i = 16.0F - i;
			k = 16.0F - k;
			l *= p;
			m *= p;
			n *= q;
			o *= q;
			Map<Direction, ModelElementFace> map = Maps.newHashMap();
			map.put(side.getDirection(), new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{l, n, m, o}, 0)));
			switch(side) {
				case UP:
					list.add(new ModelElement(new Vec3f(h, i, 7.75F), new Vec3f(j, i, 8.25F), map, null, true));
					break;
				case DOWN:
					list.add(new ModelElement(new Vec3f(h, k, 7.75F), new Vec3f(j, k, 8.25F), map, null, true));
					break;
				case LEFT:
					list.add(new ModelElement(new Vec3f(h, i, 7.75F), new Vec3f(h, k, 8.25F), map, null, true));
					break;
				case RIGHT:
					list.add(new ModelElement(new Vec3f(j, i, 7.75F), new Vec3f(j, k, 8.25F), map, null, true));
			}
		}

		return list;
	}

	private List<Frame> getFrames(Sprite sprite) {
		int width = sprite.getWidth();
		int height = sprite.getHeight();
		List<Frame> frames = Lists.newArrayList();

		for(int frame = 0; frame < sprite.getFrameCount(); ++frame) {
			for(int y = 0; y < height; ++y) {
				for(int x = 0; x < width; ++x) {
					boolean isOpaque = !this.isPixelTransparent(sprite, frame, x, y, width, height);
					this.buildCube(Side.UP, frames, sprite, frame, x, y, width, height, isOpaque);
					this.buildCube(Side.DOWN, frames, sprite, frame, x, y, width, height, isOpaque);
					this.buildCube(Side.LEFT, frames, sprite, frame, x, y, width, height, isOpaque);
					this.buildCube(Side.RIGHT, frames, sprite, frame, x, y, width, height, isOpaque);
				}
			}
		}

		return frames;
	}

	private void buildCube(Side side, List<Frame> cubes, Sprite sprite, int frame, int x, int y, int i, int j, boolean isOpaque) {
		boolean shouldBuild = this.isPixelTransparent(sprite, frame, x + side.getOffsetX(), y + side.getOffsetY(), i, j) && isOpaque;
		if (shouldBuild) {
			this.buildCube(cubes, side, x, y);
		}

	}

	private void buildCube(List<Frame> cubes, Side side, int x, int y) {
		Frame frame = null;

		for (Frame cube : cubes) {
			if (cube.getSide() == side) {
				int i = side.isVertical() ? y : x;
				if (cube.getLevel() == i) {
					frame = cube;
					break;
				}
			}
		}

		int depth = side.isVertical() ? y : x;
		int width = side.isVertical() ? x : y;
		if (frame == null) {
			cubes.add(new Frame(side, width, depth));
		} else {
			frame.expand(width);
		}

	}

	private boolean isPixelTransparent(Sprite sprite, int frame, int x, int y, int i, int j) {
		return x < 0 || y < 0 || x >= i || y >= j || sprite.isPixelTransparent(frame, x, y);
	}

	static class Frame {
		private final Side side;
		private int min;
		private int max;
		private final int level;

		public Frame(Side side, int width, int depth) {
			this.side = side;
			this.min = width;
			this.max = width;
			this.level = depth;
		}

		public void expand(int newValue) {
			if (newValue < this.min) {
				this.min = newValue;
			} else if (newValue > this.max) {
				this.max = newValue;
			}

		}

		public Side getSide() {
			return this.side;
		}

		public int getMin() {
			return this.min;
		}

		public int getMax() {
			return this.max;
		}

		public int getLevel() {
			return this.level;
		}
	}

	enum Side {
		UP(Direction.UP, 0, -1),
		DOWN(Direction.DOWN, 0, 1),
		LEFT(Direction.EAST, -1, 0),
		RIGHT(Direction.WEST, 1, 0);

		private final Direction direction;
		private final int offsetX;
		private final int offsetY;

		Side(Direction direction, int offsetX, int offsetY) {
			this.direction = direction;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}

		public Direction getDirection() {
			return this.direction;
		}

		public int getOffsetX() {
			return this.offsetX;
		}

		public int getOffsetY() {
			return this.offsetY;
		}

		private boolean isVertical() {
			return this == DOWN || this == UP;
		}
	}
}

