package repulica.cardstock.data;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import repulica.cardstock.CardStock;

public class CardPullCriterion extends AbstractCriterion<CardPullCriterion.Conditions> {
	public static final Identifier ID = new Identifier(CardStock.MODID, "card_pull");

	@Override
	protected CardPullCriterion.Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new CardPullCriterion.Conditions(playerPredicate, new Identifier(JsonHelper.getString(obj, "card", "cardstock:missingno/missingno")));
	}

	public void trigger(ServerPlayerEntity player, ItemStack card) {
		if (card.getOrCreateTag().contains("Card", NbtType.STRING)) {
			Identifier id = new Identifier(card.getOrCreateTag().getString("Card"));
			this.test(player, conditions -> id.equals(conditions.card));
		}
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier card;

		public Conditions(EntityPredicate.Extended playerPredicate, Identifier card) {
			super(CardPullCriterion.ID, playerPredicate);
			this.card = card;
		}


	}
}
