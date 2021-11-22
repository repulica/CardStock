package repulica.cardstock.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import repulica.cardstock.api.CardManager;
import repulica.cardstock.api.CardSet;

import java.util.List;

public class CardPackLootFunction extends ConditionalLootFunction {
	public static final LootFunctionType TYPE = new LootFunctionType(new Serializer());
	private Identifier targetSet;
	private int rarity;
	private @Nullable Pair<String, Float> banner;

	public CardPackLootFunction(LootCondition[] conditions, Identifier targetSet, int rarity, @Nullable Pair<String, Float> banner) {
		super(conditions);
		this.targetSet = targetSet;
		this.rarity = rarity;
		this.banner = banner;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		if (banner != null && !banner.getLeft().equals("")) {
			if (context.getRandom().nextFloat() < banner.getRight()) {
				stack.getOrCreateTag().putString("Card", targetSet.toString() + "/" + banner.getLeft());
				return stack;
			}
		}
		CardSet set = CardManager.INSTANCE.getSet(targetSet);
		List<String> names;
		if (this.rarity == -1) {
			names = Lists.newArrayList(set.getCards().keySet());
		} else {
			names = set.getCardNamesForRarity(this.rarity);
		}
		String name = names.get(context.getRandom().nextInt(names.size()));
		stack.getOrCreateTag().putString("Card", targetSet.toString() + "/" + name);
		return stack;
	}

	@Override
	public LootFunctionType getType() {
		return TYPE;
	}

	public static Builder<?> builder(Identifier targetSet, int rarity, @Nullable Pair<String, Float> banner) {
		return builder((conditions) -> new CardPackLootFunction(conditions, targetSet, rarity, banner));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<CardPackLootFunction> {

		@Override
		public void toJson(JsonObject json, CardPackLootFunction function, JsonSerializationContext context) {
			super.toJson(json, function, context);
			json.addProperty("set", function.targetSet.toString());
			json.addProperty("rarity", function.rarity);
		}

		@Override
		public CardPackLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
			Identifier set = new Identifier(JsonHelper.getString(json, "set"));
			int rarity = JsonHelper.getInt(json, "rarity");
			if (JsonHelper.hasElement(json, "banner")) {
				JsonObject banner = JsonHelper.getObject(json, "banner");
				String card = JsonHelper.getString(banner, "card");
				float chance = JsonHelper.getFloat(banner, "chance");
				return new CardPackLootFunction(conditions, set, rarity, new Pair<>(card, chance));
			}
			return new CardPackLootFunction(conditions, set, rarity, null);
		}
	}
}
