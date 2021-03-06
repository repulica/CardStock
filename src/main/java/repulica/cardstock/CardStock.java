package repulica.cardstock;

import com.mojang.serialization.Lifecycle;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;
import repulica.cardstock.api.CardManager;
import repulica.cardstock.api.HolofoilType;
import repulica.cardstock.api.SimpleHolofoilType;
import repulica.cardstock.component.CardBinderInventory;
import repulica.cardstock.data.CardManagerImpl;
import repulica.cardstock.data.CardPackLootFunction;
import repulica.cardstock.data.CardPullCriterion;
import repulica.cardstock.holofoil.ColorHolofoil;
import repulica.cardstock.holofoil.RainbowHolofoil;
import repulica.cardstock.item.CardBinderItem;
import repulica.cardstock.item.CardItem;
import repulica.cardstock.item.CardPackItem;
import repulica.cardstock.item.EnderCardBinderItem;
import repulica.cardstock.screen.CardBinderScreenHandler;

public class CardStock implements ModInitializer {
	public static final String MODID = "cardstock";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final Identifier CARD_SYNC = new Identifier(MODID, "card_sync");

	//todo: non-card group
	public static final Item CARD = Registry.register(Registry.ITEM, new Identifier(MODID, "card"), new CardItem(new Item.Settings().maxCount(4)));
	public static final Item CARD_BINDER = Registry.register(Registry.ITEM, new Identifier(MODID, "card_binder"), new CardBinderItem(new Item.Settings().maxCount(1)));
	public static final Item ENDER_CARD_BINDER = Registry.register(Registry.ITEM, new Identifier(MODID, "ender_card_binder"), new EnderCardBinderItem(new Item.Settings().maxCount(1)));
	public static final Item CARD_PACK = Registry.register(Registry.ITEM, new Identifier(MODID, "card_pack"), new CardPackItem(new Item.Settings().maxCount(1))); //todo: should this go in a group at all

	public static ScreenHandlerType<CardBinderScreenHandler> CARD_BINDER_HANDLER;

	public static final CardPullCriterion CARD_PULL = Criteria.register(new CardPullCriterion());

	public static final HolofoilType<RainbowHolofoil> RAINBOW_FOIL = Registry.register(HolofoilType.HOLOFOIL_TYPES, new Identifier(MODID, "rainbow"),new SimpleHolofoilType<>(RainbowHolofoil.INSTANCE));
	public static final HolofoilType<ColorHolofoil> COLOR_FOIL = Registry.register(HolofoilType.HOLOFOIL_TYPES, new Identifier(MODID, "color"), new ColorHolofoil.Type());

	//todo: possible to be random card?
	public static final ItemGroup CARDS_GROUP = QuiltItemGroup.builder(new Identifier(MODID, "cards"))
			.icon(() -> new ItemStack(CARD_BINDER))
			.appendItems(CardManagerImpl.INSTANCE::appendCards)
			.build();

	@Override
	public void onInitialize(ModContainer container) {
		CARD_BINDER_HANDLER = Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID), new ScreenHandlerType<>((syncid, inv) -> new CardBinderScreenHandler(CARD_BINDER_HANDLER, syncid, inv, new CardBinderInventory(), 6)));
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(CardManager.INSTANCE);
		Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(MODID, "card_pack"), CardPackLootFunction.TYPE);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sender.sendPacket(CARD_SYNC, CardManagerImpl.INSTANCE.getBuf()));
		QuiltLoader.getModContainer(MODID).ifPresent(modContainer -> ResourceLoader.registerBuiltinResourcePack(new Identifier(MODID, "retromc"), modContainer, ResourcePackActivationType.NORMAL));
	}

}
