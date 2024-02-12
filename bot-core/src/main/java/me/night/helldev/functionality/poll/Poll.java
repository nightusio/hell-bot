package me.night.helldev.functionality.poll;

import lombok.Data;
import lombok.Getter;
import me.night.helldev.functionality.crash.CrashManager;
import me.night.helldev.functionality.shared.SharedType;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
public class Poll {

    private int id;
    private long messageId;
    private long textChannel;
    private int votesYes;
    private int votesNo;
    private List<Long> votedUsers;

    public Poll(int id) {
        this.id = id;
        this.messageId = 0L;
        this.textChannel = 0L;
        this.votesNo = 0;
        this.votesYes = 0;
        this.votedUsers = new ArrayList<>();
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
            CrashManager.sendCrashMessage(exception.getCause().getMessage());
            return SharedType.VOTED_FAILED;
        }
    }
}
