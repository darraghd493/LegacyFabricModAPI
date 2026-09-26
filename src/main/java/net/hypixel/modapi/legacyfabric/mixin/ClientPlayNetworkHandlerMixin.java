package net.hypixel.modapi.legacyfabric.mixin;

import net.hypixel.modapi.legacyfabric.LegacyFabricModAPI;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author darraghd493
 */
@Mixin(ClientPlayNetworkHandler.class)
public final class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) (Object) this;
        LegacyFabricModAPI.getInstance().setNetHandler(handler);

        if (LegacyFabricModAPI.DEBUG_MODE) {
            LegacyFabricModAPI.LOGGER.info("Updated network handler.");
        }
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onDisconnected(CallbackInfo ci) {
        LegacyFabricModAPI api = LegacyFabricModAPI.getInstance();
        api.setNetHandler(null);
        api.setConnectedToHypixel(false);

        if (LegacyFabricModAPI.DEBUG_MODE) {
            LegacyFabricModAPI.LOGGER.info("Cleared network handler.");
        }
    }
}
