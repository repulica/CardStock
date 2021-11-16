package repulica.cardstock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repulica.cardstock.data.CardManager;
import repulica.cardstock.data.CardPackLootFunction;
import repulica.cardstock.item.CardItem;

public class CardStock implements ModInitializer {
	public static final String MODID = "cardstock";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final Identifier CARD_SYNC = new Identifier(MODID, "card_sync");

	public static final Item CARD = Registry.register(Registry.ITEM, new Identifier(MODID, "card"), new CardItem(new Item.Settings().maxCount(4)));

	//todo: possible to be random card?
	public static final ItemGroup CARDS_GROUP = FabricItemGroupBuilder.create(new Identifier(MODID, "cards"))
			.icon(() -> new ItemStack(CARD))
			.appendItems(CardManager.INSTANCE::appendCards)
			.build();

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(CardManager.INSTANCE);
		Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(MODID, "card_pack"), CardPackLootFunction.TYPE);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sender.sendPacket(CARD_SYNC, CardManager.INSTANCE.getBuf()));
		ClientPlayNetworking.registerGlobalReceiver(CARD_SYNC, (server, handler, buf, responseSender) -> CardManager.INSTANCE.recievePacket(buf));
	}
}
