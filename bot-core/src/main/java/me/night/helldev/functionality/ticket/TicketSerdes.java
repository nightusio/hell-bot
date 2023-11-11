package me.night.helldev.functionality.ticket;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import me.night.helldev.functionality.shared.SharedType;

public class TicketSerdes implements ObjectSerializer<Ticket> {

    @Override
    public boolean supports(@NonNull Class<? super Ticket> type) {
        return Ticket.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Ticket object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("userID", object.getUserID());
        data.add("sharedType", object.getSharedType());
        data.add("channelID", object.getChannelID());
    }

    @Override
    public Ticket deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        return new Ticket(
                data.get("userID", long.class),
                data.get("sharedType", SharedType.class),
                data.get("channelID", String.class)

                );
    }
}