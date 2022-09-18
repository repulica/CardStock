package repulica.cardstock.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;
import repulica.cardstock.data.CardManagerImpl;

import java.util.Collection;
import java.util.List;

public interface CardManager extends SimpleSynchronousResourceReloader {
	CardManager INSTANCE = CardManagerImpl.INSTANCE;

	/**
	 * @return a list of all the ids for card sets
	 */
	Collection<Identifier> getSetIds();

	/**
	 * @param id the id of the set to get
	 * @return the set with that id or the default missingno set
	 */
	CardSet getSet(Identifier id);

	/**
	 * @param id the id of the card to get
	 * @return the card with that id or the default missingno card
	 */
	Card getCard(Identifier id);

	/**
	 * @param stack the stack to get the card set from
	 * @return the set that card belongs to or the default missingno set
	 */
	CardSet getSet(ItemStack stack);

	/**
	 * @param stack the stack to get the card from
	 * @return the card this stack has or the default missingno card
	 */
	Card getCard(ItemStack stack);

	/**
	 * @param player the player to get cards for
	 * @return a list of all cards currently held by the player in their inventory or card binders (including the ender binder)
	 */
	default List<Card> getAllHeldCards(PlayerEntity player) {
		return getAllHeldCards(player, true);
	}

	/**
	 * @param player the player to get cards for
	 * @param includeEnderBinder if true search will include the ender binder
	 * @return a list of all cards currently held by the player in their inventory or card binders (including the ender binder)
	 */
	List<Card> getAllHeldCards(PlayerEntity player, boolean includeEnderBinder);

	/**
	 * @param player the player to get cards for
	 * @param setId the id of the set to get cards for
	 * @return a list of all cards in the given set currently held by the player in their inventory or card binders (including the ender binder)
	 */
	default List<Card> getHeldSetCards(PlayerEntity player, Identifier setId) {
		return getHeldSetCards(player, setId, true);
	}

	/**
	 * @param player the player to get cards for
	 * @param setId the id of the set to get cards for
	 * @param includeEnderBinder if true search will include the ender binder
	 * @return a list of all cards in the given set currently held by the player in their inventory or card binders (including the ender binder)
	 */
	List<Card> getHeldSetCards(PlayerEntity player, Identifier setId, boolean includeEnderBinder);

	/**
	 * @param player the player to get progress for
	 * @param setId the id of the set to get progress for
	 * @return a percent value between 0 and 1 of how many cards in the set the player is currently holding in their inventory or card binders (including the ender binder)
	 */
	default float getHeldSetProgress(PlayerEntity player, Identifier setId) {
		return getHeldSetProgress(player, setId, true);
	}

	/**
	 * @param player the player to get progress for
	 * @param setId the id of the set to get progress for
	 * @param includeEnderBinder if true search will include the ender binder
	 * @return a percent value between 0 and 1 of how many cards in the set the player is currently holding in their inventory or card binders
	 */
	float getHeldSetProgress(PlayerEntity player, Identifier setId, boolean includeEnderBinder);

	/**
	 * @param player the player to check cards for
	 * @param cardId the card to check for
	 * @return whether the player has that card in their inventory or card binders (including the ender binder)
	 */
	default boolean hasCard(PlayerEntity player, Identifier cardId) {
		return hasCard(player, cardId, true);
	}

	/**
	 * @param player the player to check cards for
	 * @param cardId the card to check for
	 * @param includeEnderBinder if true search will include the ender binder
	 * @return whether the player has that card in their inventory or card binders
	 */
	boolean hasCard(PlayerEntity player, Identifier cardId, boolean includeEnderBinder);

	/**
	 * @param player the player to check cards for
	 * @param keywordId the keyword to check for
	 * @return whether the player has any cards with that keyword in their inventory or card binders (including the ender binder)
	 */
	default boolean hasKeyword(PlayerEntity player, Identifier keywordId) {
		return hasKeyword(player, keywordId, true);
	}

	/**
	 * @param player the player to check cards for
	 * @param keywordId the keyword to check for
	 * @param includeEnderBinder if true search will include the ender binder
	 * @return whether the player has any cards with that keyword in their inventory or card binders
	 */
	boolean hasKeyword(PlayerEntity player, Identifier keywordId, boolean includeEnderBinder);

	/**
	 * @return the fallback set for when a set isnt found
	 */
	CardSet getDefaultMissingnoSet();

	/**
	 * @return the fallback card for when a card isnt found
	 */
	Card getDefaultMissingno();
}
