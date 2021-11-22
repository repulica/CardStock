package repulica.cardstock.api;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardSet {
	private final Identifier emblem;
	private final Map<String, Card> cards;
	private final Int2ObjectMap<List<String>> rarityMap;

	public CardSet(Identifier emblem, Map<String, Card> cards) {
		this.emblem = emblem;
		this.cards = cards;
		this.rarityMap = new Int2ObjectArrayMap<>();
		for (String card : cards.keySet()) {
			rarityMap.computeIfAbsent(cards.get(card).getRarity(), ArrayList::new).add(card);
		}
	}

	public Identifier getEmblem() {
		return emblem;
	}

	public Map<String, Card> getCards() {
		return cards;
	}

	public Card getCard(String name) {
		return cards.get(name);
	}

	public List<String> getCardNamesForRarity(int rarity) {
		return rarityMap.computeIfAbsent(rarity, ArrayList::new);
	}
}
