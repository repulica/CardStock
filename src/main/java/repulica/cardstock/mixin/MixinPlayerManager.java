package repulica.cardstock.mixin;

import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import repulica.cardstock.data.CardManager;

import java.util.Iterator;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Inject(method = "onDataPacksReloaded", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void sendCardPacket(CallbackInfo info, SynchronizeRecipesS2CPacket syncPacket, Iterator<ServerPlayerEntity> players, ServerPlayerEntity player) {
		CardManager.INSTANCE.sendPacket(player);
	}
}
