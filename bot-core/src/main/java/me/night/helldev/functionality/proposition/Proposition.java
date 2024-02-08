package me.night.helldev.functionality.proposition;

import lombok.Data;
import lombok.Getter;
import me.night.helldev.functionality.crash.CrashManager;
import me.night.helldev.functionality.shared.SharedType;
import org.javacord.api.entity.user.User;

import java.sql.Array;
import java.util.*;

@Data
@Getter
public class Proposition {

    private int id;
    private long messageId = 0L;
    private long textChannel = 0L;
    private int votesYes = 0;
    private int votesNo = 0;
    private List<Long> votedUsers= new ArrayList<>();


    public Proposition(int id) {
        this.id = id;
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
