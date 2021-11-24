package repulica.cardstock.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

//courtesy of unascribed in yttr
//used with permission and under compatible license
public class CardColorProvider implements ItemColorProvider {

	public static float holderYaw = 0;
	public static boolean holderYawValid = false;

	@Override
	public int getColor(ItemStack stack, int tintIndex) {
		if (tintIndex == 0) return -1;
		float yaw;
		if (holderYawValid) {
			yaw = holderYaw;
			if (MinecraftClient.getInstance().player != null) {
				float playerYaw = MinecraftClient.getInstance().player.yaw;
				if (yaw >= playerYaw) {
					yaw -= playerYaw;
				} else {
					yaw = 360 - (playerYaw - yaw);
				}
			}
		} else if (stack.getHolder() instanceof ItemEntity) {
			yaw = (float) Math.toDegrees(((ItemEntity) stack.getHolder()).method_27314(MinecraftClient.getInstance().getTickDelta())) % 360F;
			if (MinecraftClient.getInstance().player != null) {
				float playerYaw = MinecraftClient.getInstance().player.yaw;
				if (yaw >= playerYaw) {
					yaw -= playerYaw;
				} else {
					yaw = 360 - (playerYaw - yaw);
				}
			}
		} else if (stack.getHolder() != null) {
			yaw = stack.getHolder().yaw;
			if (MinecraftClient.getInstance().player != null) {
				float playerYaw = MinecraftClient.getInstance().player.yaw;
				if (yaw >= playerYaw) {
					yaw -= playerYaw;
				} else {
					yaw = 360 - (playerYaw - yaw);
				}
			}
		} else if (MinecraftClient.getInstance().player != null) {
			yaw = MinecraftClient.getInstance().player.yaw;
		} else {
			yaw = 0;
		}
		yaw = MathHelper.wrapDegrees(yaw)+180;
		if (tintIndex == 1) {
			yaw -= 100;
		} else if (tintIndex == 2) {
			yaw += 100;
		}
		yaw += (Math.abs(stack.hashCode()) / 2000f) % 360;
		float hue = (yaw % 360) / 360f;
		if (hue < 0) hue += 1;
		return MathHelper.hsvToRgb(hue, 0.5f, 1.0f);
	}

}