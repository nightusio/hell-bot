package me.night.helldev.functionality.shared;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
public class VoteRecord {
    private Map<Long, String> userVotes;

    public VoteRecord() {
        this.userVotes = new HashMap<>();
    }

    public void castVote(Long userId, String vote) {
        userVotes.put(userId, vote);
    }

    public String getVote(Long userId) {
        return userVotes.get(userId);
    }

    public boolean alreadyVoted(Long userId) {
        return userVotes.containsKey(userId);
    }
}
