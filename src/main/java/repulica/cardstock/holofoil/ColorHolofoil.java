package repulica.cardstock.holofoil;

import dev.hbeck.kdl.objects.KDLDocument;
import dev.hbeck.kdl.objects.KDLNode;
import dev.hbeck.kdl.objects.KDLNumber;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import repulica.cardstock.CardStock;
import repulica.cardstock.api.Holofoil;
import repulica.cardstock.api.HolofoilType;

//todo support multiple colors instead of just one that goes to white
public class ColorHolofoil implements Holofoil {
	private final int color;
	private final float squared_red;
	private final float squared_green;
	private final float squared_blue;

	public ColorHolofoil(int color) {
		this.color = color;

		// square each color on its own in order to lerp properly
		// more info: https://www.youtube.com/watch?v=LKnqECcg6Gw
		float r = (this.color >> 16 & 0xFF) / 255F;
		this.squared_red = r * r;
		float g = (this.color >> 8 & 0xFF) / 255F;
		this.squared_green = g * g;
		float b = (this.color & 0xFF) / 255F;
		this.squared_blue = b * b;
	}

	@Override
	public int getFoilColor(float yaw) {
		float percentage = yaw / 180f;
		if (percentage > 1f) percentage = 2f - percentage; // negate to go back the other way
		int r = lerpColor(percentage, squared_red);
		int g = lerpColor(percentage, squared_green);
		int b = lerpColor(percentage, squared_blue);
		return (r << 16) + (g << 8) + b;
	}

	private int lerpColor(float percentage, float part) {
		float lerped = (float) Math.sqrt(MathHelper.lerp(percentage, part, 1));
		return (int) (lerped * 255);
	}

	@Override
	public HolofoilType<?> getType() {
		return CardStock.COLOR_FOIL;
	}

	public static class Type implements HolofoilType<ColorHolofoil> {

		@Override
		public ColorHolofoil fromKdl(KDLDocument kdl) {
			for (KDLNode node : kdl.getNodes()) {
				if (node.getIdentifier().equals("color")) {
					return new ColorHolofoil(node.getArgs().get(0).getAsNumber().orElse(KDLNumber.from(0xFFFFFF)).getValue().intValue());
				}
			}
			throw new IllegalArgumentException("No color provided for color node");
		}

		@Override
		public void writeToPacket(ColorHolofoil holofoil, PacketByteBuf buf) {
			buf.writeInt(holofoil.color);
		}

		@Override
		public ColorHolofoil readFromPacket(PacketByteBuf buf) {
			return new ColorHolofoil(buf.readInt());
		}
	}
}
