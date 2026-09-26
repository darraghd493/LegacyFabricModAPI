package net.hypixel.modapi.legacyfabric.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.legacyfabric.LegacyFabricModAPI;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.PacketByteBuf;

/**
 * @see <a href="https://github.com/HypixelDev/ForgeModAPI/blob/master/src/main/java/net/hypixel/modapi/forge/ForgeModAPI.java">Original Forge ModAPI Implementation (ForgeModAPI.java)</a>
 * @author darraghd493
 */
@SuppressWarnings("UnstableApiUsage")
@ChannelHandler.Sharable
public final class PacketHandler extends SimpleChannelInboundHandler<Packet<?>> {
    public static final PacketHandler INSTANCE = new PacketHandler();

    private PacketHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Packet<?> message) {
        if (!(message instanceof CustomPayloadS2CPacket)) {
            context.fireChannelRead(message);
            return;
        }

        CustomPayloadS2CPacket packet = (CustomPayloadS2CPacket) message;
        String identifier = packet.getChannel();
        if (!HypixelModAPI.getInstance().getRegistry().isRegistered(identifier)) {
            context.fireChannelRead(message);
            return;
        }

        PacketByteBuf buffer = packet.getPayload();
        buffer.retain();
        context.fireChannelRead(message);

        MinecraftClient.getInstance().submit(() -> {
            try {
                HypixelModAPI.getInstance().handle(identifier, new PacketSerializer(buffer));
            } catch (Exception e) {
                LegacyFabricModAPI.LOGGER.warn("Failed to handle packet {}", identifier, e);
            } finally {
                buffer.release();
            }
        });
    }
}
