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
	List<Card> getAllHeldCards(PlayerEntity player);

	/**
	 * @param player the player to get cards for
	 * @param setId the id of the set to get cards for
	 * @return a list of all cards in the given set currently held by the player in their inventory or card binders (including the ender binder)
	 */
	List<Card> getHeldSetCards(PlayerEntity player, Identifier setId);

	/**
	 * @param player the player to get progress for
	 * @param setId the id of the set to get progress for
	 * @return a percent value between 0 and 1 of how many cards in the set the player is currently holding in their inventory or card binders (including the ender binder)
	 */
	float getHeldSetProgress(PlayerEntity player, Identifier setId);
	/**
	 * @param player the player to check cards for
	 * @param cardId the card to check for
	 * @return whether the player has that card in their inventory or card binders (including the ender binder)
	 */
	boolean hasCard(PlayerEntity player, Identifier cardId);
	/**
	 * @return the fallback set for when a set isnt found
	 */
	CardSet getDefaultMissingnoSet();

	/**
	 * @return the fallback card for when a card isnt found
	 */
	Card getDefaultMissingno();
}
