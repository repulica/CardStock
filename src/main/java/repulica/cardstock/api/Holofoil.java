package repulica.cardstock.api;

import dev.hbeck.kdl.objects.KDLNode;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

//todo configurable foils
//todo better foils storage
//todo more stock foils
//todo document
public interface Holofoil {
	int getFoilColor(float yaw);
	HolofoilType<?> getType();
}
