package repulica.cardstock.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import repulica.cardstock.data.CardManagerImpl;

import java.util.List;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Shadow @Final private List<ServerPlayerEntity> players;

	@Inject(method = "onDataPacksReloaded", at = @At("TAIL"))
	private void sendCardPacket(CallbackInfo info) {
		for (ServerPlayerEntity player : this.players) {
			CardManagerImpl.INSTANCE.sendPacket(player);
		}
	}
}
