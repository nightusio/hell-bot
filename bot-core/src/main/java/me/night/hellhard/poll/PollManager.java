package me.night.hellhard.poll;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.hellhard.config.PollConfig;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PollManager {

    private final PollConfig pollConfig;

    public Poll createPoll() {
        int newPollId = generateUniquePollId();
        Poll poll = new Poll(newPollId);
        pollConfig.polls.add(poll);
        pollConfig.save();
        return poll;
    }

    public Poll getPollById(String pollIdStr) {
        String cleanedPollIdStr = pollIdStr.replaceAll("pollyes-|pollno-|pollcheck-", "");
        try {
            int pollId = Integer.parseInt(cleanedPollIdStr);
            for (Poll poll : pollConfig.polls) {
                if (poll.getId() == pollId) {
                    return poll;
                }
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }

    private int generateUniquePollId() {
        int maxId = 0;
        for (Poll existingPoll : pollConfig.polls) {
            if (existingPoll.getId() > maxId) {
                maxId = existingPoll.getId();
            }
        }
        return maxId + 1;
    }
}
