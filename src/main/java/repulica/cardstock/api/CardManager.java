package repulica.cardstock.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.hbeck.kdl.objects.*;
import dev.hbeck.kdl.parse.KDLParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;
import repulica.cardstock.CardStock;
import repulica.cardstock.component.CardBinderComponent;
import repulica.cardstock.component.CardBinderInventory;
import repulica.cardstock.component.CardStockComponents;

import java.io.IOException;
import java.util.*;

public class CardManager implements SimpleSynchronousResourceReloader {
	public static final CardManager INSTANCE = new CardManager();
	private static final KDLParser PARSER = new KDLParser();
	private static final String PREFIX = "cardstock/sets";
	private static final int SUFFIX_LENGTH = ".kdl".length();

	private final Map<Identifier, CardSet> sets = new HashMap<>();

	private final Card defaultMissingno;
	private final CardSet defaultMissingnoSet;

	public CardManager() {
		this.defaultMissingno = new Card(1, new TranslatableText("text.cardstock.missingno"), new ArrayList<>(), "BluKat", "2021");
		Map<String, Card> setMap = new HashMap<>();
		setMap.put("missingno", defaultMissingno);
		this.defaultMissingnoSet = new CardSet(new Identifier(CardStock.MODID, "textures/gui/missingno.png"), setMap);
		sets.put(new Identifier(CardStock.MODID, "missingno"), defaultMissingnoSet);
	}

	/**
	 * @return a list of all the ids for card sets
	 */
	public Collection<Identifier> getSetIds() {
		return sets.keySet();
	}

	/**
	 * @param id the id of the set to get
	 * @return the set with that id or the default missingno set
	 */
	public CardSet getSet(Identifier id) {
		return sets.getOrDefault(id, defaultMissingnoSet);
	}

	/**
	 * @param id the id of the card to get
	 * @return the card with that id or the default missingno card
	 */
	public Card getCard(Identifier id) {
		Identifier setId = new Identifier(id.getNamespace(), id.getPath().substring(0, id.getPath().lastIndexOf('/')));
		String cardId = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
		return getSet(setId).getCard(cardId);
	}

	/**
	 * @param stack the stack to get the card set from
	 * @return the set that card belongs to or the default missingno set
	 */
	public CardSet getSet(ItemStack stack) {
		if (stack.hasNbt() && stack.getNbt().contains("Card", NbtElement.STRING_TYPE)) {
			String cardName = stack.getNbt().getString("Card");
			return getSet(new Identifier(cardName.substring(0, cardName.lastIndexOf('/'))));
		}
		return defaultMissingnoSet;
	}

	/**
	 * @param stack the stack to get the card from
	 * @return the card this stack has or the default missingno card
	 */
	public Card getCard(ItemStack stack) {
		if (stack.hasNbt() && stack.getNbt().contains("Card", NbtElement.STRING_TYPE)) {
			return getCard(new Identifier(stack.getNbt().getString("Card")));
		}
		return defaultMissingno;
	}

	/**
	 * @param player the player to get cards for
	 * @return a list of all cards currently held by the player in their inventory or card binders (including the ender binder)
	 */
	public List<Card> getAllHeldCards(PlayerEntity player) {
		List<Card> cards = new ArrayList<>();
		PlayerInventory inv = player.getInventory();
		if (!inv.isEmpty()) {
			for (int i = 0; i < inv.size(); i++) {
				ItemStack stack = inv.getStack(i);
				if (stack.getItem() == CardStock.CARD && stack.getOrCreateNbt().contains("Card")) {
					cards.add(getCard(new Identifier(stack.getOrCreateNbt().getString("Card"))));
				} else if (CardStockComponents.CARD_BINDER.isProvidedBy(stack)) {
					addBinderCards(cards, CardStockComponents.CARD_BINDER.get(stack));
				}
			}
		}
		addBinderCards(cards, CardStockComponents.CARD_BINDER.get(player));
		return cards;
	}

	/**
	 * @param player the player to get cards for
	 * @param setId the id of the set to get cards for
	 * @return a list of all cards in the given set currently held by the player in their inventory or card binders (including the ender binder)
	 */
	public List<Card> getHeldSetCards(PlayerEntity player, Identifier setId) {
		CardSet set = getSet(setId);
		List<Card> cards = new ArrayList<>();
		PlayerInventory inv = player.getInventory();
		if (!inv.isEmpty()) {
			for (int i = 0; i < inv.size(); i++) {
				ItemStack stack = inv.getStack(i);
				if (stack.getItem() == CardStock.CARD && stack.getOrCreateNbt().contains("Card")) {
					if (set == getSet(stack)) {
						cards.add(getCard(new Identifier(stack.getOrCreateNbt().getString("Card"))));
					}
				} else if (CardStockComponents.CARD_BINDER.isProvidedBy(stack)) {
					addBinderSetCards(cards, CardStockComponents.CARD_BINDER.get(stack), set);
				}
			}
		}
		addBinderSetCards(cards, CardStockComponents.CARD_BINDER.get(player), set);
		return cards;
	}

	/**
	 * @param player the player to get progress for
	 * @param setId the id of the set to get progress for
	 * @return a percent value between 0 and 1 of how many cards in the set the player is currently holding in their inventory or card binders (including the ender binder)
	 */
	public float getHeldSetProgress(PlayerEntity player, Identifier setId) {
		Set<Card> cards = new HashSet<>(getHeldSetCards(player, setId));
		CardSet set = getSet(setId);
		return (float) cards.size() / (float) set.getCards().size();
	}

	/**
	 * @param player the player to check cards for
	 * @param cardId the card to check for
	 * @return whether the player has that card in their inventory or card binders (including the ender binder)
	 */
	public boolean hasCard(PlayerEntity player, Identifier cardId) {
		Card card = getCard(cardId);
		return getAllHeldCards(player).contains(card);
	}

	/**
	 * @return the fallback set for when a set isnt found
	 */
	public CardSet getDefaultMissingnoSet() {
		return defaultMissingnoSet;
	}

	/**
	 * @return the fallback card for when a card isnt found
	 */
	public Card getDefaultMissingno() {
		return defaultMissingno;
	}

	//non-api-intended stuff goes below here

	@Override
	public void reload(ResourceManager manager) {
		sets.clear();
		sets.put(new Identifier(CardStock.MODID, "missingno"), defaultMissingnoSet);
		int cardCount = 1;
		Collection<Identifier> ids = manager.findResources(PREFIX, path -> path.endsWith(".kdl"));
		for (Identifier id : ids) {
			try {
				Identifier setId = new Identifier(id.getNamespace(), id.getPath().substring(PREFIX.length() + 1, id.getPath().length() - SUFFIX_LENGTH));
				KDLDocument doc = PARSER.parse(manager.method_14486(id).getInputStream());
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
										rarity = child.getArgs().get(0).getAsNumber().orElse(KDLNumber.from(1)).getValue().intValue();
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
		CardStock.LOGGER.info("Loaded " + sets.size() + " card set" + (sets.size() > 1? "s" : "") + ", including " + cardCount + " card" + (cardCount > 1? "s" : ""));
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
			Map<String, KDLValue<?>> props = node.getProps();
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
				json.addProperty("bold", props.get("bold").getAsBoolean().orElse(new KDLBoolean(false)).getValue());
			}
			if (props.containsKey("italic")) {
				json.addProperty("italic", props.get("italic").getAsBoolean().orElse(new KDLBoolean(false)).getValue());
			}
			if (props.containsKey("underline")) {
				json.addProperty("underline", props.get("underline").getAsBoolean().orElse(new KDLBoolean(false)).getValue());
			}
			if (props.containsKey("strikethrough")) {
				json.addProperty("strikethrough", props.get("strikethrough").getAsBoolean().orElse(new KDLBoolean(false)).getValue());
			}
			if (props.containsKey("obfuscated")) {
				json.addProperty("obfuscated", props.get("obfuscated").getAsBoolean().orElse(new KDLBoolean(false)).getValue());
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
	public Identifier getQuiltId() {
		return new Identifier(CardStock.MODID, "card_manager");
	}

	private void addBinderCards(List<Card> cards, CardBinderComponent comp) {
		CardBinderInventory binder = comp.getInv();
		if (!binder.isEmpty()) {
			for (int i = 0; i < binder.size(); i++) {
				ItemStack card = binder.getStack(i);
				if (card.getItem() == CardStock.CARD && card.getOrCreateNbt().contains("Card")) {
					cards.add(getCard(new Identifier(card.getOrCreateNbt().getString("Card"))));
				}
			}
		}
	}

	private void addBinderSetCards(List<Card> cards, CardBinderComponent comp, CardSet set) {
		CardBinderInventory binder = comp.getInv();
		if (!binder.isEmpty()) {
			for (int i = 0; i < binder.size(); i++) {
				ItemStack stack = binder.getStack(i);
				if (stack.getItem() == CardStock.CARD && stack.getOrCreateNbt().contains("Card")) {
					if (set == getSet(stack)) {
						cards.add(getCard(new Identifier(stack.getOrCreateNbt().getString("Card"))));
					}
				}
			}
		}
	}

	public void appendCards(List<ItemStack> toDisplay) {
		toDisplay.add(new ItemStack(CardStock.CARD_BINDER));
		toDisplay.add(new ItemStack(CardStock.ENDER_CARD_BINDER));
		for (Identifier id : sets.keySet()) {
			CardSet set = sets.get(id);
			for (String name : set.getCards().keySet()) {
				Identifier cardId = new Identifier(id.getNamespace(), id.getPath() + '/' + name);
				ItemStack cardStack = new ItemStack(CardStock.CARD);
				cardStack.getOrCreateNbt().putString("Card", cardId.toString());
				toDisplay.add(cardStack);
			}
		}
	}

	public PacketByteBuf getBuf() {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(sets.size()); //amount of card sets
		for (Identifier id : sets.keySet()) {
			buf.writeIdentifier(id); //id of the card set
			CardSet set = sets.get(id);
			buf.writeIdentifier(set.getEmblem()); //emblem of the card set
			Map<String, Card> cards = set.getCards();
			buf.writeVarInt(cards.size()); //amount of cards in the set
			for (String key : cards.keySet()) {
				buf.writeString(key); //name of the card
				Card card = cards.get(key);
				buf.writeVarInt(card.rarity()); //rarity of the card
				buf.writeString(card.artist()); //artist of the card
				buf.writeString(card.date()); //date of the card
				buf.writeText(card.info()); //card info
				buf.writeVarInt(card.lore().size()); //number of lore lines
				for (Text line : card.lore()) {
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
