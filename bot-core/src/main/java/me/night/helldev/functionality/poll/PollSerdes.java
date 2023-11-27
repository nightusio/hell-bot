package me.night.helldev.functionality.poll;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PollSerdes implements ObjectSerializer<Poll> {

    @Override
    public boolean supports(@NonNull Class<? super Poll> type) {
        return Poll.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Poll object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("id", object.getId());
        data.add("messageId", object.getMessageId());
        data.add("textChannel", object.getTextChannel());
        data.add("votesYes", object.getVotesYes());
        data.add("votesNo", object.getVotesNo());
        data.add("votedUsers", object.getVotedUsers());

    }

    @Override
    public Poll deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        return new Poll(
                data.get("id", int.class),
                data.get("messageId", long.class),
                data.get("textChannel", long.class),
                data.get("votesYes", int.class),
                data.get("votesNo", int.class),
                toSet(data.get("votedUsers", List.class))
        );
    }

    @SuppressWarnings("unchecked")
    private Set<Long> toSet(Object object) {
        if (object instanceof List) {
            return new HashSet<>((List<Long>) object);
        }
        throw new IllegalArgumentException("Expected List<Long> for votedUsers");
    }

}