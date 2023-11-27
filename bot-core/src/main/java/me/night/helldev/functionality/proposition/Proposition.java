package me.night.helldev.functionality.proposition;

import lombok.Data;
import lombok.Getter;
import me.night.helldev.functionality.shared.SharedType;
import org.javacord.api.entity.user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
public class Proposition {

    private int id;
    private long messageId;
    private long textChannel;
    private int votesYes;
    private int votesNo;
    private Set<Long> votedUsers;

    public Proposition(int id, long messageId, long textChannel, int votesYes, int votesNo, Set<Long> votedUsers) {
        this.id = id;
        this.messageId = messageId;
        this.textChannel = textChannel;
        this.votesYes = votesYes;
        this.votesNo = votesNo;
        this.votedUsers = votedUsers;
    }

    private void updateVotes(boolean voteYes) {
        if (voteYes) {
            votesYes++;
        } else {
            votesNo++;
        }
    }

    public SharedType addVote(User user, boolean voteYes) {
        if (votedUsers.contains(user.getId())) {
            return SharedType.ALREADY_VOTED;
        }
        try {
            updateVotes(voteYes);
            votedUsers.add(user.getId());
            return voteYes ? SharedType.VOTED_SUCCESSFULLY_YES : SharedType.VOTED_SUCCESSFULLY_NO;
        } catch (Exception exception) {
            return SharedType.VOTED_FAILED;
        }
    }
}
