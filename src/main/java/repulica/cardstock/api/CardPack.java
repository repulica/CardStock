package repulica.cardstock.api;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.util.Identifier;

import java.util.List;

public class CardPack {
	private final Identifier set;
	private final int cardCount;
	private final Int2IntMap weights;
	private final List<Bonus> bonuses;

	public CardPack(Identifier set, int cardCount, Int2IntMap weights, List<Bonus> bonuses) {
		this.set = set;
		this.cardCount = cardCount;
		this.weights = weights;
		this.bonuses = bonuses;
	}

	public Identifier getSet() {
		return set;
	}

	public int getCardCount() {
		return cardCount;
	}

	public Int2IntMap getWeights() {
		return weights;
	}

	public List<Bonus> getBonuses() {
		return bonuses;
	}

	public static class Bonus {
		private final int rarity;
		private final int count;
		private final String banner;
		private final float bannerChance;
		private final boolean bannerOnly;

		public Bonus(int rarity, int count) {
			this.rarity = rarity;
			this.count = count;
			this.banner = "";
			this.bannerChance = 0;
			this.bannerOnly = false;
		}

		public Bonus(int rarity, int count, String banner, float bannerChance) {
			this.rarity = rarity;
			this.count = count;
			this.banner = banner;
			this.bannerChance = bannerChance;
			this.bannerOnly = false;
		}

		public Bonus(String banner, float bannerChace) {
			this.rarity = -1;
			this.count = 1;
			this.banner = banner;
			this.bannerChance = bannerChace;
			this.bannerOnly = true;
		}

		public int getRarity() {
			return rarity;
		}

		public int getCount() {
			return count;
		}

		public String getBanner() {
			return banner;
		}

		public float getBannerChance() {
			return bannerChance;
		}

		public boolean isBannerOnly() {
			return bannerOnly;
		}
	}
}
