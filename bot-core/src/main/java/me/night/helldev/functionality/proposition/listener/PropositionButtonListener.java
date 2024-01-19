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
        Proposition proposition = propositionManager.getPropositionById(event.getButtonInteraction().getCustomId());

        if (proposition == null) return;

        if (event.getButtonInteraction().getCustomId().contains("propositionyes-" + proposition.getId())) {
            SharedType sharedType = proposition.addVote(user, true);
            propositionConfig.save();


            if (sharedType == SharedType.VOTED_SUCCESSFULLY_YES) {
                propositionConfig.save();

                MessageUtility.respondWithEphemeralMessage(event, "Pomyslnie zaglosowales na tak!");
                ButtonEditUtility.editActionRowsProposition(user.getApi(), proposition);

            } else if (sharedType == SharedType.VOTED_FAILED) {
                MessageUtility.respondWithEphemeralMessage(event, "Cos poszlo nie tak.");
            } else {
                MessageUtility.respondWithEphemeralMessage(event, "Nie mozesz zaglosowac drugi raz!");
            }
        }

        if (event.getButtonInteraction().getCustomId().equals("propositionno-" + proposition.getId())) {
            SharedType sharedType = proposition.addVote(user, false);

            if (sharedType == SharedType.VOTED_SUCCESSFULLY_NO) {
                propositionConfig.save();

                MessageUtility.respondWithEphemeralMessage(event, "Pomyslnie zaglosowales na nie!");
                ButtonEditUtility.editActionRowsProposition(user.getApi(), proposition);

            } else if (sharedType == SharedType.VOTED_FAILED) {
                MessageUtility.respondWithEphemeralMessage(event, "Cos poszlo nie tak.");
            } else if (sharedType == SharedType.ALREADY_VOTED) {
                MessageUtility.respondWithEphemeralMessage(event, "Nie mozesz zaglosowac drugi raz!");
            }
        }
    }


}
