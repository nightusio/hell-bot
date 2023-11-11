package me.night.helldev.functionality.proposition;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

public class PropositionSerdes implements ObjectSerializer<Proposition> {

    @Override
    public boolean supports(@NonNull Class<? super Proposition> type) {
        return Proposition.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Proposition object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("id", object.getId());
        data.add("messageId", object.getMessageId());
        data.add("textChannel", object.getTextChannel());
        data.add("votesYes", object.getVotesYes());
        data.add("votesNo", object.getVotesNo());
        data.add("votedUsers", object.getVotedUsers());

    }

    @Override
    public Proposition deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        return new Proposition(
                data.get("id", int.class)
        );
    }
}