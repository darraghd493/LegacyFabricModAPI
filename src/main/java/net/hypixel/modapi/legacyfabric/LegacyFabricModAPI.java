package net.hypixel.modapi.legacyfabric;

import io.netty.buffer.Unpooled;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.HypixelModAPIImplementation;
import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://github.com/HypixelDev/ForgeModAPI/blob/master/src/main/java/net/hypixel/modapi/forge/ForgeModAPI.java">Original Forge ModAPI Implementation (ForgeModAPI.java)</a>
 * @author Hypixel Inc., darraghd493
 */
@Setter
@SuppressWarnings({"unused", "UnstableApiUsage", "VulnerableCodeUsages"})
public final class LegacyFabricModAPI implements ClientModInitializer, HypixelModAPIImplementation {
    public static final Logger LOGGER = LogManager.getLogger("HypixelModAPI");
    public static final boolean DEBUG_MODE = Boolean.getBoolean("net.hypixel.modapi.debug");

    private static LegacyFabricModAPI INSTANCE; // for direct access

    private @Nullable ClientPlayNetworkHandler netHandler;
    private boolean connectedToHypixel;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        HypixelModAPI.getInstance().setModImplementation(this);
    }

    @Override
    public void onInit() {
        HypixelModAPI.getInstance().createHandler(
                ClientboundHelloPacket.class,
                packet -> this.connectedToHypixel = true
        );

        if (DEBUG_MODE) {
            LOGGER.info("Debug mode is enabled!");
            registerDebug();
        }
    }

    @Override
    public boolean sendPacket(HypixelPacket packet) {
        if (this.netHandler == null || !this.isConnectedToHypixel()) {
            return false;
        }

        if (!this.netHandler.getClientConnection().isOpen()) {
            LOGGER.warn("Attempted to send packet while channel is closed!");
            this.netHandler = null;
            return false;
        }

        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        PacketSerializer serializer = new PacketSerializer(buffer);
        packet.write(serializer);
        this.netHandler.sendPacket(new CustomPayloadC2SPacket(packet.getIdentifier(), buffer));
        return true;
    }

    @Override
    public boolean isConnectedToHypixel() {
        return this.connectedToHypixel;
    }

    //region Access
    public static @NotNull LegacyFabricModAPI getInstance() {
        return INSTANCE;
    }
    //endregion

    //region Debugging
    private static void registerDebug() {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(
                ClientboundLocationPacket.class,
                packet -> LOGGER.info("Received location packet {}", packet)
        ).onError(error -> LOGGER.error("Received error response for location packet: {}", error));
    }
    //endregion
}