package repulica.cardstock.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

//alternate version of ItemModelGenerator that makes them half as thick and gives better names for texture layers
public class CardModelGenerator {
	public static final List<String> LAYERS = Lists.newArrayList("art", "frame", "holo_center", "holo_left", "holo_right", "extra");
	private static final float FRONT = 7.75F;
	private static final float BACK = 8.25F;
	public static final ModelTransformation ITEM_TRANSFORMATION;

	public JsonUnbakedModel create(Function<SpriteIdentifier, Sprite> textureGetter, JsonUnbakedModel baseModel) {
		Map<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
		List<ModelElement> list = Lists.newArrayList();

		for(int i = 0; i < LAYERS.size(); i++) {
			String string = LAYERS.get(i);
			if (!baseModel.textureExists(string)) {
				continue;
			}

			SpriteIdentifier spriteIdentifier = baseModel.resolveSprite(string);
			map.put(string, Either.left(spriteIdentifier));
			Sprite sprite = textureGetter.apply(spriteIdentifier);
			list.addAll(this.addLayerElements(i, string, sprite));
		}

		map.put("particle", baseModel.textureExists("particle") ? Either.left(baseModel.resolveSprite("particle")) : map.get("art"));
		JsonUnbakedModel model = new JsonUnbakedModel(null, list, map, false, baseModel.getGuiLight(), ITEM_TRANSFORMATION, baseModel.getOverrides());
		model.id = baseModel.id;
		return model;
	}

	private List<ModelElement> addLayerElements(int layer, String key, Sprite sprite) {
		Map<Direction, ModelElementFace> map = Maps.newHashMap();
		map.put(Direction.SOUTH, new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
		map.put(Direction.NORTH, new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
		List<ModelElement> list = Lists.newArrayList();
		list.add(new ModelElement(new Vec3f(0.0F, 0.0F, FRONT), new Vec3f(16.0F, 16.0F, BACK), map, null, true));
		list.addAll(this.addSubComponents(sprite, key, layer));
		return list;
	}

	private List<ModelElement> addSubComponents(Sprite sprite, String key, int layer) {
		float width = (float)sprite.getWidth();
		float height = (float)sprite.getHeight();
		List<ModelElement> list = Lists.newArrayList();

		for (Frame frame : getFrames(sprite)) {
			float x1 = 0.0F;
			float y1 = 0.0F;
			float x2 = 0.0F;
			float y2 = 0.0F;
			float u1 = 0.0F;
			float u2 = 0.0F;
			float v1 = 0.0F;
			float v2 = 0.0F;
			float frameWidth = 16.0F / width;
			float frameHeight = 16.0F / height;
			float min = (float)frame.getMin();
			float max = (float)frame.getMax();
			float level = (float)frame.getLevel();
			Side side = frame.getSide();
			switch (side) {
				case UP -> {
					u1 = min;
					x1 = min;
					x2 = u2 = max + 1.0F;
					v1 = level;
					y1 = level;
					y2 = level;
					v2 = level + 1.0F;
				}
				case DOWN -> {
					v1 = level;
					v2 = level + 1.0F;
					u1 = min;
					x1 = min;
					x2 = u2 = max + 1.0F;
					y1 = level + 1.0F;
					y2 = level + 1.0F;
				}
				case LEFT -> {
					u1 = level;
					x1 = level;
					x2 = level;
					u2 = level + 1.0F;
					v2 = min;
					y1 = min;
					y2 = v1 = max + 1.0F;
				}
				case RIGHT -> {
					u1 = level;
					u2 = level + 1.0F;
					x1 = level + 1.0F;
					x2 = level + 1.0F;
					v2 = min;
					y1 = min;
					y2 = v1 = max + 1.0F;
				}
			}

			x1 *= frameWidth;
			x2 *= frameWidth;
			y1 *= frameHeight;
			y2 *= frameHeight;
			y1 = 16.0F - y1;
			y2 = 16.0F - y2;
			u1 *= frameWidth;
			u2 *= frameWidth;
			v1 *= frameHeight;
			v2 *= frameHeight;
			Map<Direction, ModelElementFace> map = Maps.newHashMap();
			map.put(side.getDirection(), new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{u1, v1, u2, v2}, 0)));
			switch (side) {
				case UP ->
						list.add(new ModelElement(new Vec3f(x1, y1, FRONT), new Vec3f(x2, y1, BACK), map, null, true));
				case DOWN ->
						list.add(new ModelElement(new Vec3f(x1, y2, FRONT), new Vec3f(x2, y2, BACK), map, null, true));
				case LEFT ->
						list.add(new ModelElement(new Vec3f(x1, y1, FRONT), new Vec3f(x1, y2, BACK), map, null, true));
				case RIGHT ->
						list.add(new ModelElement(new Vec3f(x2, y1, FRONT), new Vec3f(x2, y2, BACK), map, null, true));
			}
		}

		return list;
	}

	private List<Frame> getFrames(Sprite sprite) {
		int width = sprite.getWidth();
		int height = sprite.getHeight();
		List<Frame> list = Lists.newArrayList();
		sprite.getDistinctFrameCount().forEach((frame) -> {
			for(int y = 0; y < height; ++y) {
				for(int x = 0; x < width; ++x) {
					boolean bl = !this.isPixelTransparent(sprite, frame, x, y, width, height);
					this.buildCube(Side.UP, list, sprite, frame, x, y, width, height, bl);
					this.buildCube(Side.DOWN, list, sprite, frame, x, y, width, height, bl);
					this.buildCube(Side.LEFT, list, sprite, frame, x, y, width, height, bl);
					this.buildCube(Side.RIGHT, list, sprite, frame, x, y, width, height, bl);
				}
			}

		});
		return list;
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

	static {
		Transformation ground = new Transformation(new Vec3f(0, 0, 0), new Vec3f(0/16F, 2/16F, 0/16F), new Vec3f(0.5F, 0.5F, 0.5F));
		Transformation head = new Transformation(new Vec3f(0, 180, 0), new Vec3f(0/16F, 13/16F, 7/16F), new Vec3f(1, 1, 1));
		Transformation thirdPerson = new Transformation(new Vec3f(0, 0, 0), new Vec3f(0/16F, 3/16F, 1/16F), new Vec3f(0.55F, 0.55F, 0.55F));
		Transformation firstPerson = new Transformation(new Vec3f(0, -90, 25), new Vec3f(1.13F/16F, 3.2F/16F, 1.13F/16F), new Vec3f(0.68F, 0.68F, 0.68F));
		Transformation fixed = new Transformation(new Vec3f(0, 180, 0), new Vec3f(0/16F, 0/16F, 0/16F), new Vec3f(1, 1, 1));
		ITEM_TRANSFORMATION = new ModelTransformation(thirdPerson, thirdPerson, firstPerson, firstPerson, head, Transformation.IDENTITY, ground, fixed);
	}
}

