package me.night.helldev.functionality.poll.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.poll.Poll;
import me.night.helldev.functionality.poll.PollConfig;
import me.night.helldev.functionality.poll.PollManager;
import me.night.helldev.functionality.shared.SharedType;
import me.night.helldev.utility.ButtonEditUtility;
import me.night.helldev.utility.MessageUtility;
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
        String customId = event.getButtonInteraction().getCustomId();
        Poll poll = pollManager.getPollById(customId);

        if (poll == null) return;

        if (customId.contains("pollcheck-" + poll.getId())) {
            MessageUtility.respondWithEphemeralMessage(event, "Osoby ktore zaglosowaly: " +
                    poll.getVotedUsers());
            return;
        }


        SharedType sharedType = customId.contains("pollyes-" + poll.getId()) ?
                poll.addVote(user, true) :
                poll.addVote(user, false);


        String responseMessage;
        switch (sharedType) {
            case VOTED_SUCCESSFULLY_YES:
                responseMessage = "Pomyślnie zagłosowałeś na tak!";
                break;
            case VOTED_SUCCESSFULLY_NO:
                responseMessage = "Pomyślnie zagłosowałeś na nie!";
                break;
            case VOTED_FAILED:
                responseMessage = "Coś poszło nie tak.";
                break;
            case ALREADY_VOTED:
                responseMessage = "Nie możesz zagłosować drugi raz!";
                break;
            default:
                responseMessage = "";
        }

        pollConfig.save();
        MessageUtility.respondWithEphemeralMessage(event, responseMessage);
        ButtonEditUtility.editActionRowsPoll(user.getApi(), poll);
    }

}
