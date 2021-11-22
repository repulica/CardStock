package repulica.cardstock.component;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerCardBinderComponent implements CardBinderComponent {
	private CardBinderInventory inv = new CardBinderInventory();
	private final PlayerEntity player;

	public PlayerCardBinderComponent(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public CardBinderInventory getInv() {
		return inv;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		inv.readNbtList(tag.getList("Items", NbtType.COMPOUND));
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.put("Items", inv.toNbtList());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayerCardBinderComponent) {
			return inv.equals(((PlayerCardBinderComponent) obj).inv);
		}
		return false;
	}
}
