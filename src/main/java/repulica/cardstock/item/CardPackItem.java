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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

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
			ItemStack stack = user.getStackInHand(hand);
			if (stack.getOrCreateTag().contains("Items")) {
				NbtList list = stack.getOrCreateTag().getList("Items", NbtType.COMPOUND);
				if (list.size() > 0) {
					DefaultedList<ItemStack> stacks = DefaultedList.ofSize(list.size(), ItemStack.EMPTY);
					Inventories.readNbt(stack.getOrCreateTag(), stacks);
					List<ItemStack> cards = stacks.stream().filter(e -> !e.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
					giveCards(user, stack, cards);
				} else {
					stack.decrement(1);
				}
			} else if (stack.getOrCreateTag().contains("Pack")) {
				LootTable table = world.getServer().getLootManager().getTable(new Identifier(stack.getOrCreateTag().getString("Pack")));
				LootContext ctx = new LootContext.Builder((ServerWorld) world).build(LootContextTypes.EMPTY);
				List<ItemStack> cards = table.generateLoot(ctx);
				giveCards(user, stack, cards);
				stack.getOrCreateTag().remove("Pack");
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

	private void giveCards(PlayerEntity player, ItemStack stack, List<ItemStack> cards) {
		if (player.isSneaking()) {
			for (ItemStack card : cards) {
				if (!player.giveItemStack(card)) player.dropItem(card, false);
			}
		} else {
			ItemStack card = cards.remove(0);
			if (!player.giveItemStack(card)) player.dropItem(card, false);
		}
		if (!cards.isEmpty()) {
			DefaultedList<ItemStack> remainder = DefaultedList.copyOf(ItemStack.EMPTY, cards.toArray(new ItemStack[]{}));
			Inventories.writeNbt(stack.getOrCreateTag(), remainder);
		}  else {
			stack.getOrCreateTag().remove("Items");
			stack.setCount(0);
		}
	}
}
