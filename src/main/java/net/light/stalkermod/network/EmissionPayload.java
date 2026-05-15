package net.light.stalkermod.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EmissionPayload(int state) implements CustomPayload {
    public static final CustomPayload.Id<EmissionPayload> ID = new CustomPayload.Id<>(Identifier.of("stalker-mod", "emission_sync"));
    public static final PacketCodec<PacketByteBuf, EmissionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EmissionPayload::state,
            EmissionPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}