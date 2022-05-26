package repulica.cardstock.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

//courtesy of unascribed in yttr
//used with permission and under compatible license
public class CardColorProvider implements ItemColorProvider {
	private static final int LAYER_ART = 0;
	private static final int LAYER_FRAME = 1;
	private static final int LAYER_HOLO_CENTER = 2;
	private static final int LAYER_HOLO_LEFT = 3;
	private static final int LAYER_HOLO_RIGHT = 4;
	private static final int LAYER_EXTRA = 5;

	public static float holderYaw = 0;
	public static boolean holderYawValid = false;

	@Override
	public int getColor(ItemStack stack, int layer) {
		if (layer == LAYER_ART || layer == LAYER_FRAME || layer == LAYER_EXTRA) return -1;
		float yaw;
		if (holderYawValid) {
			yaw = holderYaw;
			if (MinecraftClient.getInstance().player != null) {
				float playerYaw = MinecraftClient.getInstance().player.getYaw();
				if (yaw >= playerYaw) {
					yaw -= playerYaw;
				} else {
					yaw = 360 - (playerYaw - yaw);
				}
			}
		} else if (stack.getHolder() instanceof ItemEntity) {
			yaw = (float) Math.toDegrees(((ItemEntity) stack.getHolder()).getRotation(MinecraftClient.getInstance().getTickDelta())) % 360F;
			if (MinecraftClient.getInstance().player != null) {
				float playerYaw = MinecraftClient.getInstance().player.getYaw();
				if (yaw >= playerYaw) {
					yaw -= playerYaw;
				} else {
					yaw = 360 - (playerYaw - yaw);
				}
			}
		} else if (stack.getHolder() != null) {
			yaw = stack.getHolder().getYaw();
			if (MinecraftClient.getInstance().player != null) {
				float playerYaw = MinecraftClient.getInstance().player.getYaw();
				if (yaw >= playerYaw) {
					yaw -= playerYaw;
				} else {
					yaw = 360 - (playerYaw - yaw);
				}
			}
		} else if (MinecraftClient.getInstance().player != null) {
			yaw = MinecraftClient.getInstance().player.getYaw();
		} else {
			yaw = 0;
		}
		yaw = MathHelper.wrapDegrees(yaw)+180;
		if (layer == LAYER_HOLO_LEFT) {
			yaw -= 120;
		} else if (layer == LAYER_HOLO_RIGHT) {
			yaw += 120;
		}
		yaw += (Math.abs(stack.hashCode()) / 2000f) % 360;
		float hue = (yaw % 360) / 360f;
		if (hue < 0) hue += 1;
		return MathHelper.hsvToRgb(hue, 0.5f, 1.0f);
	}

}