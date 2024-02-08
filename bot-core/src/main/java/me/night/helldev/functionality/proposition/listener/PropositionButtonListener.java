package me.night.helldev.functionality.proposition.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.proposition.Proposition;
import me.night.helldev.functionality.proposition.PropositionConfig;
import me.night.helldev.functionality.proposition.PropositionManager;
import me.night.helldev.functionality.shared.SharedType;
import me.night.helldev.utility.ButtonEditUtility;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PropositionButtonListener implements ButtonClickListener {

    private final PropositionManager propositionManager;
    private final PropositionConfig propositionConfig;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        User user = event.getInteraction().getUser();
        String customId = event.getButtonInteraction().getCustomId();
        Proposition proposition = propositionManager.getPropositionById(customId);

        if (proposition == null) return;

        SharedType sharedType = customId.startsWith("propositionyes-") ?
                proposition.addVote(user, true) :
                proposition.addVote(user, false);

        handleVoteResult(event, user, proposition, sharedType);
    }

    private void handleVoteResult(ButtonClickEvent event, User user, Proposition proposition, SharedType sharedType) {
        switch (sharedType) {
            case VOTED_SUCCESSFULLY_YES:
            case VOTED_SUCCESSFULLY_NO:
                MessageUtility.respondWithEphemeralMessage(event, "Pomyślnie zagłosowałeś!");
                ButtonEditUtility.editActionRowsProposition(user.getApi(), proposition);
                propositionConfig.save();
                break;
            case VOTED_FAILED:
                MessageUtility.respondWithEphemeralMessage(event, "Coś poszło nie tak.");
                break;
            case ALREADY_VOTED:
                MessageUtility.respondWithEphemeralMessage(event, "Nie możesz zagłosować drugi raz!");
                break;
        }
    }
}
