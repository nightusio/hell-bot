package me.night.helldev.functionality.poll;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

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

    public void setMessageId(Poll poll, long messageId) {
        poll.setMessageId(messageId);
        pollConfig.save();
    }

    public void setTextChannel(Poll poll, long textChannel) {
        poll.setTextChannel(textChannel);
        pollConfig.save();
    }

    public Poll getPollById(String pollIdStr) {
        int pollId = extractPollId(pollIdStr);
        return pollId != -1 ? findPollById(pollId) : null;
    }

    private Poll findPollById(int pollId) {
        return pollConfig.polls.stream()
                .filter(p -> p.getId() == pollId)
                .findFirst()
                .orElse(null);
    }

    private int extractPollId(String pollIdStr) {
        String cleanedPropositionIdStr = pollIdStr.replaceAll("pollyes-|pollno-|pollcheck-", "");
        try {
            return Integer.parseInt(cleanedPropositionIdStr);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private int generateUniquePollId() {
        int maxId = pollConfig.polls.stream()
                .mapToInt(Poll::getId)
                .max()
                .orElse(0);

        return maxId + 1;
    }

}
