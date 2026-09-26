package net.hypixel.modapi.legacyfabric.mixin;

import io.netty.channel.Channel;
import net.hypixel.modapi.legacyfabric.LegacyFabricModAPI;
import net.hypixel.modapi.legacyfabric.handler.PacketHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author darraghd493
 */
@Mixin(ClientConnection.class)
public final class ClientConnectionMixin {
    @Shadow
    private Channel channel;

    @Inject(method = "setPacketListener", at = @At("TAIL"))
    private void onSetPacketListener(PacketListener listener, CallbackInfo ci) {
        if (listener instanceof ClientPlayNetworkHandler && this.channel != null) {
            if (this.channel.pipeline().get("hypixel_mod_api_packet_handler") == null) {
                if (LegacyFabricModAPI.DEBUG_MODE) {
                    LegacyFabricModAPI.LOGGER.info("Attached Hypixel Mod API packet handler to the channel pipeline.");
                }

                this.channel.pipeline().addBefore(
                        "packet_handler",
                        "hypixel_mod_api_packet_handler",
                        PacketHandler.INSTANCE
                );
            }
        }
    }
}
