package repulica.cardstock.api;

import com.mojang.serialization.Lifecycle;
import dev.hbeck.kdl.objects.KDLDocument;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import repulica.cardstock.CardStock;

public interface HolofoilType<T extends Holofoil> {
	RegistryKey<Registry<HolofoilType<?>>> HOLOFOIL_TYPES_KEY = RegistryKey.ofRegistry(new Identifier(CardStock.MODID, "holofoil_types"));
	Registry<HolofoilType<?>> HOLOFOIL_TYPES = new DefaultedRegistry<>(CardStock.MODID + ":rainbow", HOLOFOIL_TYPES_KEY, Lifecycle.stable(), null);

	T fromKdl(KDLDocument kdl);
	void writeToPacket(T holofoil, PacketByteBuf buf);
	T readFromPacket(PacketByteBuf buf);
}
