package me.night.hellhard.listener.poll;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.hellhard.config.PollConfig;
import me.night.hellhard.poll.Poll;
import me.night.hellhard.poll.PollManager;
import me.night.hellhard.poll.PollType;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PollButtonListener implements ButtonClickListener {

    private final PollManager pollManager;
    private final PollConfig pollConfig;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        User user = event.getInteraction().getUser();
        Poll poll = pollManager.getPollById(event.getButtonInteraction().getCustomId());

        if (poll == null) return;

        if (event.getButtonInteraction().getCustomId().contains("pollyes-" + poll.getId())) {
            PollType pollType = poll.addVote(user, true);
            pollConfig.save();

            if (pollType == PollType.VOTED_SUCCESSFULLY_YES) {
                respondWithEphemeralMessage(event, "Pomyslnie zaglosowales na tak!");
            } else if (pollType == PollType.VOTED_FAILED) {
                respondWithEphemeralMessage(event, "Cos poszlo nie tak.");
            } else {
                respondWithEphemeralMessage(event, "Nie mozesz zaglosowac drugi raz!");
            }
        }

        if (event.getButtonInteraction().getCustomId().equals("pollno-" + poll.getId())) {
            PollType pollType = poll.addVote(user, false);
            pollConfig.save();

            if (pollType == PollType.VOTED_SUCCESSFULLY_YES) {
                respondWithEphemeralMessage(event, "Pomyslnie zaglosowales na nie!");
            } else if (pollType == PollType.VOTED_FAILED) {
                respondWithEphemeralMessage(event, "Cos poszlo nie tak.");
            }  else {
                respondWithEphemeralMessage(event, "Nie mozesz zaglosowac drugi raz!");
            }
        }

        if (event.getButtonInteraction().getCustomId().equals("pollcheck-" + poll.getId())) {
            respondWithEphemeralMessage(event, "Glosy na tak: " + poll.getVotesYes() + " | Glosy na nie: " +poll.getVotesNo());
        }
    }

    private void respondWithEphemeralMessage(ButtonClickEvent event, String content) {
        event.getInteraction().createImmediateResponder()
                .setContent(content)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

}