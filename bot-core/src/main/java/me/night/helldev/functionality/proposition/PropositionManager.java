package me.night.helldev.functionality.proposition;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PropositionManager {

    private final PropositionConfig propositionConfig;

    public Proposition createProposition() {
        int newPropositionId = generateUniquePropositionId();
        Proposition proposition = new Proposition(newPropositionId);
        propositionConfig.propositions.add(proposition);
        return proposition;
    }

    public void setMessageId(Proposition proposition, long messageId) {
        proposition.setMessageId(messageId);
    }

    public void setTextChannel(Proposition proposition, long textChannel) {
        proposition.setTextChannel(textChannel);
    }

    public Proposition getPropositionById(String propositionIdStr) {
        String cleanedPropositionIdStr = propositionIdStr.replaceAll("propositionyes-|propositionno-|propositioncheck-", "");
        try {
            int propositionId = Integer.parseInt(cleanedPropositionIdStr);
            return propositionConfig.propositions.stream()
                    .filter(p -> p.getId() == propositionId)
                    .findFirst()
                    .orElse(null);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int generateUniquePropositionId() {
        return propositionConfig.propositions.stream()
                .mapToInt(Proposition::getId)
                .max()
                .orElse(0) + 1;
    }
}
