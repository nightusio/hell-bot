package me.night.helldev.functionality.proposition;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PropositionManager {

    private final PropositionConfig propositionConfig;


    public Proposition createProposition() {
        int newPropositionId = generateUniquePropositionId();
        Proposition proposition = new Proposition(newPropositionId, 0L, 0L, 0, 0);

        propositionConfig.propositions.add(proposition);
        propositionConfig.save();

        return proposition;
    }


    public void setMessageId(Proposition proposition, long messageId) {
        proposition.setMessageId(messageId);
        propositionConfig.save();

    }

    public void setTextChannel(Proposition proposition, long textChannel) {
        proposition.setTextChannel(textChannel);
        propositionConfig.save();
    }

    public Proposition getPropositionById(String propositionIdStr) {
        String cleanedPropositionIdStr = propositionIdStr.replaceAll("propositionyes-|propositionno-|propositioncheck-", "");
        try {
            int propositionId = Integer.parseInt(cleanedPropositionIdStr);
            Optional<Proposition> foundProposition = propositionConfig.propositions.stream().filter(p -> p.getId() == propositionId).findFirst();
            return foundProposition.orElse(null);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int generateUniquePropositionId() {
        int maxId = 0;
        for (Proposition existingPropositions : propositionConfig.propositions) {
            if (existingPropositions.getId() > maxId) {
                maxId = existingPropositions.getId();
            }
        }
        return maxId + 1;
    }

}
