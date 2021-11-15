package repulica.cardstock.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import dev.hbeck.kdl.objects.KDLDocument;
import dev.hbeck.kdl.objects.KDLNode;
import dev.hbeck.kdl.objects.KDLNumber;
import dev.hbeck.kdl.objects.KDLValue;
import dev.hbeck.kdl.parse.KDLParser;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.minecraft.loot.*;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import repulica.cardstock.CardStock;
import repulica.cardstock.data.CardPack;
import repulica.cardstock.data.CardPackLootFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(LootManager.class)
public class MixinLootManager {
	@Shadow @Final private static Logger LOGGER;
	private static final KDLParser PARSER = new KDLParser();
	private static final int PREFIX_LENGTH = "cardstock/".length();
	private static final int SUFFIX_LENGTH = ".kdl".length();

	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value="INVOKE", target="Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void injectCardPackTables(Map<Identifier, JsonElement> jsons, ResourceManager manager, Profiler profiler, CallbackInfo info, ImmutableMap.Builder<Identifier, LootTable> tableMap) {
		Collection<Identifier> ids = manager.findResources("cardstock/packs", path -> path.endsWith(".kdl"));
		for (Identifier id : ids) {
			try {
				Identifier tableId = new Identifier(id.getNamespace(), id.getPath().substring(PREFIX_LENGTH, id.getPath().length() - SUFFIX_LENGTH));
				KDLDocument doc = PARSER.parse(manager.getResource(id).getInputStream());
				CardPack pack = parsePackDoc(tableId, doc);
				int bonuses = 0;
				for (CardPack.Bonus bonus : pack.getBonuses()) {
					bonuses += bonus.getCount();
				}
				if (bonuses > pack.getCardCount()) {
					throw new IllegalArgumentException("Card pack " + tableId + " has more bonuses than drawable cards!");
				}
				int rawPulls = pack.getCardCount() - bonuses;
				LootTable.Builder builder = new LootTable.Builder();
				FabricLootPoolBuilder mainPool = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(rawPulls));
				for (int rarity : pack.getWeights().keySet()) {
					int weight = pack.getWeights().get(rarity);
					mainPool.withEntry(ItemEntry.builder(CardStock.CARD).weight(weight).apply(CardPackLootFunction.builder(pack.getSet(), rarity, null)).build());
				}
				builder.pool(mainPool);
				for (CardPack.Bonus bonus : pack.getBonuses()) {
					FabricLootPoolBuilder pool = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(bonus.getCount()));
					pool.withEntry(
							ItemEntry.builder(CardStock.CARD).apply(CardPackLootFunction.builder(
									pack.getSet(),
									bonus.getRarity(),
									new Pair<>(bonus.getBanner(), bonus.getBannerChance())
							)).build()
					);
					builder.pool(pool);
				}
				LootTable table = builder.build();
				//todo: print the table json
				tableMap.put(tableId, table);
			} catch(IOException e) {
				LOGGER.error("Could not open kdl card pack document: " + e.getMessage());
			}
		}
	}

	//todo: make more dynamic in the future this is really complex
	private CardPack parsePackDoc(Identifier packId, KDLDocument doc) {
		Identifier set = null;
		int cardCount = -1;
		Int2IntMap weights = new Int2IntArrayMap();
		List<CardPack.Bonus> bonuses = new ArrayList<>();
		for (KDLNode node : doc.getNodes()) {
			switch (node.getIdentifier()) {
				case "set":
					set = new Identifier(node.getArgs().get(0).getAsString().getValue());
					break;
				case "cards":
					cardCount = flattenToInt(node.getArgs().get(0));
					break;
				case "weights":
					Map<String, KDLValue> props = node.getProps();
					if (props.containsKey("r1")) {
						weights.put(1, flattenToInt(props.get("r1")));
					}
					if (props.containsKey("r2")) {
						weights.put(2, flattenToInt(props.get("r2")));
					}
					if (props.containsKey("r3")) {
						weights.put(3, flattenToInt(props.get("r3")));
					}
					if (props.containsKey("r4")) {
						weights.put(4, flattenToInt(props.get("r4")));
					}
					if (props.containsKey("r5")) {
						weights.put(5, flattenToInt(props.get("r5")));
					}
					break;
				case "bonuses":
					for (KDLNode child : node.getChild().orElse(new KDLDocument(new ArrayList<>())).getNodes()) {
						if (child.getIdentifier().equals("rarity")) {
							int rarity = flattenToInt(child.getArgs().get(0));
							int count = 1;
							Map<String, KDLValue> childProps = child.getProps();
							if (childProps.containsKey("count")) {
								count = flattenToInt(childProps.get("count"));
							}
							if (child.getChild().isPresent()) {
								KDLNode banner = child.getChild().get().getNodes().get(0);
								String name = banner.getArgs().get(0).getAsString().getValue();
								float chance = flattenToFloat(banner.getProps().get("chance"));
								bonuses.add(new CardPack.Bonus(rarity, count, name, chance));
							} else {
								bonuses.add(new CardPack.Bonus(rarity, count));
							}
						} else if (child.getIdentifier().equals("card")) {
							String name = child.getArgs().get(0).getAsString().getValue();
							float chance = flattenToFloat(child.getProps().get("chance"));
							bonuses.add(new CardPack.Bonus(name, chance));
						}
					}
					break;
			}
		}
		if (set == null) {
			throw new IllegalArgumentException("Card pack " + packId.toString() + " missing set name! This is required!");
		}
		if (cardCount == -1) {
			throw new IllegalArgumentException("Card pack " + packId.toString() + " missing card count! This is required!");
		}
		if (weights.size() == 0) {
			throw new IllegalArgumentException("Card pack " + packId.toString() + " missing rarity weights! This is required!");
		}
		return new CardPack(set, cardCount, weights, bonuses);
	}

	private int flattenToInt(KDLValue val) {
		return val.getAsNumber().orElse(KDLNumber.from(-1)).getAsBigDecimal().intValue();
	}

	private float flattenToFloat(KDLValue val) {
		return val.getAsNumber().orElse(KDLNumber.from(-1)).getAsBigDecimal().floatValue();
	}
}
