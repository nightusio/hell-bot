package me.night.helldev.listener.poll;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.poll.Poll;
import me.night.helldev.functionality.poll.PollConfig;
import me.night.helldev.functionality.poll.PollManager;

import me.night.helldev.functionality.shared.SharedType;
import me.night.helldev.utility.ButtonEditUtility;
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
            SharedType sharedType = poll.addVote(user, true);

            if (sharedType == SharedType.VOTED_SUCCESSFULLY_YES) {
                pollConfig.save();

                respondWithEphemeralMessage(event, "Pomyslnie zaglosowales na tak!");
                ButtonEditUtility.editActionRowsPoll(user.getApi(), poll);

            } else if (sharedType == SharedType.VOTED_FAILED) {
                respondWithEphemeralMessage(event, "Cos poszlo nie tak.");
            } else {
                respondWithEphemeralMessage(event, "Nie mozesz zaglosowac drugi raz!");
            }
        }

        if (event.getButtonInteraction().getCustomId().equals("pollno-" + poll.getId())) {
            SharedType sharedType = poll.addVote(user, false);

            if (sharedType == SharedType.VOTED_SUCCESSFULLY_NO) {
                pollConfig.save();

                respondWithEphemeralMessage(event, "Pomyslnie zaglosowales na nie!");
                ButtonEditUtility.editActionRowsPoll(user.getApi(), poll);

            } else if (sharedType == SharedType.VOTED_FAILED) {
                respondWithEphemeralMessage(event, "Cos poszlo nie tak.");
            } else if (sharedType == SharedType.ALREADY_VOTED) {
                respondWithEphemeralMessage(event, "Nie mozesz zaglosowac drugi raz!");
            }
        }
    }

    private void respondWithEphemeralMessage(ButtonClickEvent event, String content) {
        event.getInteraction().createImmediateResponder()
                .setContent(content)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

}
