package me.night.helldev.functionality.shared;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VoteRecordSerdes implements ObjectSerializer<VoteRecord> {

    @Override
    public boolean supports(@NonNull Class<? super VoteRecord> type) {
        return VoteRecord.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull VoteRecord object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("userVotes", object.getUserVotes());
    }

    @Override
    public VoteRecord deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        VoteRecord voteRecord = new VoteRecord();
        voteRecord.setUserVotes(data.getAsMap("userVotes", Long.class, String.class));
        return voteRecord;
    }


}