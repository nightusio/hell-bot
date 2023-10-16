package me.night.hellhard.poll;

import lombok.Data;
import lombok.Getter;
import me.night.hellhard.config.PollConfig;
import org.javacord.api.entity.user.User;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
public class Poll {

    private int id;
    private int votesYes;
    private int votesNo;
    private Set<Long> votedUsers;

    public Poll(int id) {
        this.id = id;
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

    public PollType addVote(User user, boolean voteYes) {
        if (votedUsers.contains(user.getId())) {
            return PollType.ALREADY_VOTED;
        }
        try {
            updateVotes(voteYes);
            votedUsers.add(user.getId());

            return voteYes ? PollType.VOTED_SUCCESSFULLY_YES : PollType.VOTED_SUCCESSFULLY_NO;
        } catch (Exception exception) {
            return PollType.VOTED_FAILED;
        }
    }
}
