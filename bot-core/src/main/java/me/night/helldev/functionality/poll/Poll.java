package me.night.helldev.functionality.poll;

import lombok.Data;
import lombok.Getter;
import me.night.helldev.functionality.shared.SharedType;
import org.javacord.api.entity.user.User;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
public class Poll {

    private int id;
    private long messageId;
    private long textChannel;
    private int votesYes;
    private int votesNo;
    private Set<Long> votedUsers;

    public Poll(int id) {
        this.id = id;
        this.messageId = 0L;
        this.textChannel = 0L;
        this.votesYes = 0;
        this.votesNo = 0;
        this.votedUsers = new HashSet<>();
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
