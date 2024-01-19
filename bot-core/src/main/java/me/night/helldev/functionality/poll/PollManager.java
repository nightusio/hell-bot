package me.night.helldev.functionality.poll;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PollManager {

    private final PollConfig pollConfig;

    public Poll createPoll() {
        int newPollId = generateUniquePollId();
        Poll poll = new Poll(newPollId, 0L, 0L, 0, 0);
        pollConfig.polls.add(poll);
        pollConfig.save();
        return poll;
    }


    public void setMessageId(Poll poll, long messageId) {
        poll.setMessageId(messageId);
        pollConfig.save();
    }

    public void setTextChannel(Poll poll, long textChannel) {
        poll.setTextChannel(textChannel);
        pollConfig.save();
    }

    public Poll getPollById(String pollIdStr) {
        String cleanedPropositionIdStr = pollIdStr.replaceAll("pollyes-|pollno-|pollcheck-", "");
        try {
            int pollId = Integer.parseInt(cleanedPropositionIdStr);
            Optional<Poll> foundPoll = pollConfig.polls.stream().filter(p -> p.getId() == pollId).findFirst();
            return foundPoll.orElse(null);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int generateUniquePollId() {
        int maxId = 0;
        for (Poll existingPolls : pollConfig.polls) {
            if (existingPolls.getId() > maxId) {
                maxId = existingPolls.getId();
            }
        }
        return maxId + 1;
    }

}
