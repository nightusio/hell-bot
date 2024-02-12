package me.night.helldev.functionality.ticket;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketSerdes implements ObjectSerializer<Ticket> {

    @Override
    public boolean supports(@NonNull Class<? super Ticket> type) {
        return Ticket.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Ticket object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("id", object.getId());
        data.add("userId", object.getUserId());
        data.add("category", object.getCategory());
        data.add("addedUsers", object.getAddedUsers(), Long.class);
        data.add("server", object.getServer());
        data.add("channelId", object.getChannelId());
        data.add("messageId", object.getMessageId());
    }

    @Override
    public Ticket deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        return new Ticket(
                data.get("id", int.class),
                data.get("userId", long.class),
                data.get("category", String.class),
                data.get("server", long.class),
                data.getAsList("addedUsers", Long.class),
                data.get("channelId", long.class),
                data.get("messageId", long.class)
        );
    }


}