package repulica.cardstock.api;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.Set;

public record Card(int rarity, Text info, List<Text> lore, String artist, String date, Holofoil holofoil, Set<Identifier> keywords) {

	public Rarity getItemRarity() {
		return rarity == 0 ? Rarity.COMMON : Rarity.values()[Math.min(rarity - 1, 3)];
	}

}
