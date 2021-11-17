package repulica.cardstock.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.util.Identifier;
import repulica.cardstock.CardStock;

public class CardStockComponents implements EntityComponentInitializer, ItemComponentInitializer {
	public static final ComponentKey<CardBinderComponent> CARD_BINDER = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(CardStock.MODID, "card_binder"), CardBinderComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(CARD_BINDER, PlayerCardBinderComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}

	@Override
	public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
		registry.registerFor(CardStock.CARD_BINDER, CARD_BINDER, ItemCardBinderComponent::new);
	}
}
