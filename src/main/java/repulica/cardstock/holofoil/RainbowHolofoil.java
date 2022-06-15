package repulica.cardstock.holofoil;

import net.minecraft.util.math.MathHelper;
import repulica.cardstock.CardStock;
import repulica.cardstock.api.Holofoil;
import repulica.cardstock.api.HolofoilType;

public class RainbowHolofoil implements Holofoil {
	public static final RainbowHolofoil INSTANCE = new RainbowHolofoil();
	@Override
	public int getFoilColor(float yaw) {
		float hue = (yaw % 360) / 360f;
		if (hue < 0) hue += 1;
		return MathHelper.hsvToRgb(hue, 0.5f, 1.0f);
	}

	@Override
	public HolofoilType<?> getType() {
		return CardStock.RAINBOW_FOIL;
	}
}
