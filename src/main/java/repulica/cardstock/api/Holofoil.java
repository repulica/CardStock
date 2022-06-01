package repulica.cardstock.api;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

//todo configurable foils
//todo better foils storage
//todo more stock foils
//todo document
@FunctionalInterface
public interface Holofoil {
	Map<Identifier, Holofoil> FOILS = new HashMap<>();

	int getFoilColor(float yaw);
}
