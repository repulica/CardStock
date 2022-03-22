package repulica.cardstock.item;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import repulica.cardstock.CardStock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardPackItem extends Item {
	public CardPackItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient) {
			ServerPlayerEntity player = (ServerPlayerEntity) user;
			ItemStack stack = user.getStackInHand(hand);
			if (stack.getOrCreateNbt().contains("Items")) {
				NbtList list = stack.getOrCreateNbt().getList("Items", NbtType.COMPOUND);
				if (list.size() > 0) {
					DefaultedList<ItemStack> stacks = DefaultedList.ofSize(list.size(), ItemStack.EMPTY);
					Inventories.readNbt(stack.getOrCreateNbt(), stacks);
					List<ItemStack> cards = stacks.stream().filter(e -> !e.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
					giveCards(player, stack, cards);
				} else {
					stack.decrement(1);
				}
			} else if (stack.getOrCreateNbt().contains("Pack")) {
				LootTable table = world.getServer().getLootManager().getTable(new Identifier(stack.getOrCreateNbt().getString("Pack")));
				LootContext ctx = new LootContext.Builder((ServerWorld) world).build(LootContextTypes.EMPTY);
				List<ItemStack> cards = table.generateLoot(ctx);
				giveCards(player, stack, cards);
				stack.getOrCreateNbt().remove("Pack");
			} else {
				stack.setCount(0);
				return TypedActionResult.consume(ItemStack.EMPTY);
			}
			if (stack.isEmpty()) {
				return TypedActionResult.consume(ItemStack.EMPTY);
			}
			return TypedActionResult.success(stack);
		}
		return super.use(world, user, hand);
	}

	private void giveCards(ServerPlayerEntity player, ItemStack stack, List<ItemStack> cards) {
		if (player.isSneaking()) {
			for (ItemStack card : cards) {
				CardStock.CARD_PULL.trigger(player, stack);
				player.getInventory().offerOrDrop(card);
			}
		} else {
			ItemStack card = cards.remove(0);
			CardStock.CARD_PULL.trigger(player, stack);
			player.getInventory().offerOrDrop(card);
		}
		if (!cards.isEmpty()) {
			DefaultedList<ItemStack> remainder = DefaultedList.copyOf(ItemStack.EMPTY, cards.toArray(new ItemStack[]{}));
			Inventories.writeNbt(stack.getOrCreateNbt(), remainder);
		}  else {
			stack.getOrCreateNbt().remove("Items");
			stack.setCount(0);
		}
	}
}
