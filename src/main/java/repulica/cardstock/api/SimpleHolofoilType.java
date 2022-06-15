package repulica.cardstock.api;

import dev.hbeck.kdl.objects.KDLDocument;
import net.minecraft.network.PacketByteBuf;

public class SimpleHolofoilType<T extends Holofoil> implements HolofoilType<T> {
	private final T holofoil;

	public SimpleHolofoilType(T holofoil) {
		this.holofoil = holofoil;
	}

	@Override
	public T fromKdl(KDLDocument kdl) {
		return holofoil;
	}

	@Override
	public void writeToPacket(T holofoil, PacketByteBuf buf) {}

	@Override
	public T readFromPacket(PacketByteBuf buf) {
		return holofoil;
	}
}
