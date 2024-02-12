package me.night.helldev.functionality.poll.command;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.functionality.poll.Poll;
import me.night.helldev.functionality.poll.PollManager;
import me.night.helldev.utility.ButtonEditUtility;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PollReloadCommand extends JavacordCommand {

    private final PollManager pollManager;
    @Inject
    public PollReloadCommand(PollManager pollManager) {
        super("pollreload", "Reloads provided poll by ID");
        this.pollManager = pollManager;

        List<SlashCommandOption> optionList = new ArrayList<>(Collections.singletonList(

                SlashCommandOption.create(SlashCommandOptionType.STRING, "id", "Poll id", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            String pollId = interaction.getArgumentByIndex(0).flatMap(SlashCommandInteractionOption::getStringValue).orElse("");
                Poll poll = pollManager.getPollById(pollId);

                if (poll == null) {
                    MessageUtility.respondWithEphemeralMessage(event, "Nie znaleziono ankiety o takim ID!");
                    return;
                }

                ButtonEditUtility.editActionRowsPoll(event.getApi(), poll);
                MessageUtility.respondWithEphemeralMessage(event, "Pomyslnie zrefreshowales przyciski ankiety!");


            responder.respond();
        };
    }

}
