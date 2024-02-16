package me.night.helldev.functionality.poll.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.poll.Poll;
import me.night.helldev.functionality.poll.PollConfig;
import me.night.helldev.functionality.poll.PollManager;
import me.night.helldev.functionality.shared.SharedType;
import me.night.helldev.utility.ButtonEditUtility;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            List<String> list = new ArrayList<>();
            for (Map.Entry<Long, String> votedUser : poll.getVotedUsers().getUserVotes().entrySet()) {
                String entry = "<@" + votedUser.getKey() + "> : " + votedUser.getValue();
                list.add(entry);
            }
            MessageUtility.respondWithEphemeralMessage(event, "Osoby ktore zaglosowaly: " +
                    list);
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
