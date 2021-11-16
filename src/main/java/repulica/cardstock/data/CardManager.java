package repulica.cardstock.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.hbeck.kdl.objects.*;
import dev.hbeck.kdl.parse.KDLParser;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import repulica.cardstock.CardStock;

import java.io.IOException;
import java.util.*;

public class CardManager implements SimpleSynchronousResourceReloadListener {
	public static final CardManager INSTANCE = new CardManager();
	private static final KDLParser PARSER = new KDLParser();
	private static final String PREFIX = "cardstock/sets";
	private static final int SUFFIX_LENGTH = ".kdl".length();

	private final Map<Identifier, CardSet> sets = new HashMap<>();

	private final Card defaultMissingno;
	private final CardSet defaultMissingnoSet;

	public CardManager() {
		this.defaultMissingno = new Card(1, new TranslatableText("text.cardstock.missingno"), new ArrayList<>(), "kat", "2020");
		Map<String, Card> setMap = new HashMap<>();
		setMap.put("missingno", defaultMissingno);
		this.defaultMissingnoSet = new CardSet(new Identifier(CardStock.MODID, "textures/gui/missingno.png"), setMap);
		sets.put(new Identifier(CardStock.MODID, "missingno"), defaultMissingnoSet);
	}

	@Override
	public void reload(ResourceManager manager) {
		sets.clear();
		sets.put(new Identifier(CardStock.MODID, "missingno"), defaultMissingnoSet);
		int cardCount = 1;
		Collection<Identifier> ids = manager.findResources(PREFIX, path -> path.endsWith(".kdl"));
		for (Identifier id : ids) {
			try {
				Identifier setId = new Identifier(id.getNamespace(), id.getPath().substring(PREFIX.length() + 1, id.getPath().length() - SUFFIX_LENGTH));
				KDLDocument doc = PARSER.parse(manager.getResource(id).getInputStream());
				Identifier emblem = new Identifier(CardStock.MODID, "textures/gui/missingno.png");
				Map<String, Card> parsedCards = new HashMap<>();
				for (KDLNode node : doc.getNodes()) {
					if (node.getIdentifier().equals("emblem")) {
						emblem = new Identifier(node.getArgs().get(0).getAsString().getValue());
					} else if (node.getIdentifier().equals("card")) {
						String name = node.getArgs().get(0).getAsString().getValue();
						int rarity = 0;
						Text info = new LiteralText("");
						List<Text> lore = new ArrayList<>();
						String artist = "";
						String date = "";
						Optional<KDLDocument> children = node.getChild();
						if (children.isPresent()) {
							for (KDLNode child : children.get().getNodes()) {
								switch (child.getIdentifier()) {
									case "rarity":
										rarity = child.getArgs().get(0).getAsNumber().orElse(KDLNumber.from(1)).getAsBigDecimal().intValue();
										break;
									case "info":
										info = parseText(child);
										break;
									case "lore":
										for (KDLNode line : child.getChild().get().getNodes()) {
											lore.add(parseText(line));
										}
										break;
									case "artist":
										artist = child.getArgs().get(0).getAsString().getValue();
										break;
									case "date":
										date = child.getArgs().get(0).getAsString().getValue();
										break;
								}
							}
						}
						parsedCards.put(name, new Card(rarity, info, lore, artist, date));
						cardCount++;
					}
				}
				sets.put(setId, new CardSet(emblem, parsedCards));
			} catch (IOException e) {
				CardStock.LOGGER.error("Could not load card set " + id + ": " + e);
			}
		}
		//todo: proper singulars
		CardStock.LOGGER.info("Loaded " + sets.size() + " card sets, including " + cardCount + " cards");
	}

	private Text parseText(KDLNode node) {
		//json hacks are the easiest thing to do here honestly
		if (node.getProps().size() == 0 && node.getChild().isPresent()) {
			//just a list of texts
			JsonArray arr = new JsonArray();
			for (KDLNode child : node.getChild().get().getNodes()) {
				Text component = parseText(child);
				arr.add(Text.Serializer.toJsonTree(component));
			}
			return Text.Serializer.fromJson(arr);
		} else {
			JsonObject json = new JsonObject();
			Map<String, KDLValue> props = node.getProps();
			if (props.containsKey("text")) {
				json.addProperty("text", props.get("text").getAsString().getValue());
			} else if (props.containsKey("translate")) {
				json.addProperty("translate", props.get("translate").getAsString().getValue());
			}
			if (props.containsKey("color")) {
				json.addProperty("color", props.get("color").getAsString().getValue());
			}
			if (props.containsKey("font")) {
				json.addProperty("font", props.get("font").getAsString().getValue());
			}
			if (props.containsKey("bold")) {
				json.addProperty("bold", props.get("bold").getAsBoolean().orElse(KDLBoolean.FALSE).getValue());
			}
			if (props.containsKey("italic")) {
				json.addProperty("italic", props.get("italic").getAsBoolean().orElse(KDLBoolean.FALSE).getValue());
			}
			if (props.containsKey("underline")) {
				json.addProperty("underline", props.get("underline").getAsBoolean().orElse(KDLBoolean.FALSE).getValue());
			}
			if (props.containsKey("strikethrough")) {
				json.addProperty("strikethrough", props.get("strikethrough").getAsBoolean().orElse(KDLBoolean.FALSE).getValue());
			}
			if (props.containsKey("obfuscated")) {
				json.addProperty("obfuscated", props.get("obfuscated").getAsBoolean().orElse(KDLBoolean.FALSE).getValue());
			}
			if (node.getChild().isPresent()) {
				JsonArray arr = new JsonArray();
				for (KDLNode child : node.getChild().get().getNodes()) {
					Text component = parseText(child);
					arr.add(Text.Serializer.toJsonTree(component));
				}
				json.add("extra", arr);
			}
			return Text.Serializer.fromJson(json);
		}
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(CardStock.MODID, "card_manager");
	}

	public Collection<Identifier> getSetIds() {
		return sets.keySet();
	}

	public CardSet getSet(Identifier id) {
		return sets.getOrDefault(id, defaultMissingnoSet);
	}

	public Card getCard(Identifier id) {
		Identifier setId = new Identifier(id.getNamespace(), id.getPath().substring(0, id.getPath().lastIndexOf('/')));
		String cardId = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
		return getSet(setId).getCard(cardId);
	}

	public CardSet getSet(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("Card", NbtType.STRING)) {
			String cardName = stack.getTag().getString("Card");
			return getSet(new Identifier(cardName.substring(0, cardName.lastIndexOf('/'))));
		}
		return getSet(new Identifier(CardStock.MODID, "missingno"));
	}

	public Card getCard(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("Card", NbtType.STRING)) {
			return getCard(new Identifier(stack.getTag().getString("Card")));
		}
		return getCard(new Identifier(CardStock.MODID, "missingno/missingno"));
	}

	public CardSet getDefaultMissingnoSet() {
		return defaultMissingnoSet;
	}

	public Card getDefaultMissingno() {
		return defaultMissingno;
	}

	public void appendCards(List<ItemStack> toDisplay) {
		for (Identifier id : sets.keySet()) {
			CardSet set = sets.get(id);
			for (String name : set.getCards().keySet()) {
				Identifier cardId = new Identifier(id.getNamespace(), id.getPath() + '/' + name);
				ItemStack cardStack = new ItemStack(CardStock.CARD);
				cardStack.getOrCreateTag().putString("Card", cardId.toString());
				toDisplay.add(cardStack);
			}
		}
	}

	public PacketByteBuf getBuf() {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(sets.size()); //amount of card sets
		for (Identifier id : sets.keySet()) {
			buf.writeIdentifier(id); //ID of the card set
			CardSet set = sets.get(id);
			buf.writeIdentifier(set.getEmblem()); //emblem of the card set
			Map<String, Card> cards = set.getCards();
			buf.writeVarInt(cards.size()); //amount of cards in the set
			for (String key : cards.keySet()) {
				buf.writeString(key); //name of the card
				Card card = cards.get(key);
				buf.writeVarInt(card.getRarity()); //rarity of the card
				buf.writeString(card.getArtist()); //artist of the card
				buf.writeString(card.getDate()); //date of the card
				buf.writeText(card.getInfo()); //card info
				buf.writeVarInt(card.getLore().size()); //number of lore lines
				for (Text line : card.getLore()) {
					buf.writeText(line); //line of lore
				}
			}
		}
		return buf;
	}

	public void sendPacket(ServerPlayerEntity player) {
		PacketByteBuf buf = getBuf();
		ServerPlayNetworking.send(player, CardStock.CARD_SYNC, buf);
	}

	public void recievePacket(PacketByteBuf buf) {
		sets.clear();
		int setCount = buf.readVarInt();
		for (int i = 0; i < setCount; i++) {
			Identifier id = buf.readIdentifier();
			Identifier emblem = buf.readIdentifier();
			Map<String, Card> cards = new HashMap<>();
			int cardCount = buf.readVarInt();
			for (int j = 0; j < cardCount; j++) {
				String name = buf.readString();
				int rarity = buf.readVarInt();
				String artist = buf.readString();
				String date = buf.readString();
				Text info = buf.readText();
				List<Text> lore = new ArrayList<>();
				int loreCount = buf.readVarInt();
				for (int k = 0; k < loreCount; k++) {
					lore.add(buf.readText());
				}
				cards.put(name, new Card(rarity, info, lore, artist, date));
			}
			sets.put(id, new CardSet(emblem, cards));
		}
	}
}
