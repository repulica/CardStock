package repulica.cardstock.api;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;

public record Card(int rarity, Text info, List<Text> lore, String artist, String date, Holofoil holofoil) {

	public Rarity getItemRarity() {
		return rarity == 0 ? Rarity.COMMON : Rarity.values()[Math.min(rarity - 1, 3)];
	}

}
