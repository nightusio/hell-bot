package me.night.helldev.command.user.ticket;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.functionality.ticket.Ticket;
import me.night.helldev.functionality.ticket.TicketManager;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.*;

public class TicketRemoveUserCommand extends JavacordCommand {

    private final TicketManager ticketManager;

    @Inject
    public TicketRemoveUserCommand(TicketManager ticketManager) {
        super("remove", "Usuwa wybrana osobe do ticketa.");

        this.ticketManager = ticketManager;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Collections.singletonList(

                SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Osoba ktora chcesz usunac z ticketa", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            User user = interaction.getArgumentByIndex(0)
                    .flatMap(SlashCommandInteractionOption::getUserValue)
                    .orElse(null);

            Optional<TextChannel> optionalTextChannel = event.getInteraction().getChannel();

            if (optionalTextChannel.isEmpty()) return;

            TextChannel textChannel = optionalTextChannel.get();

            ticketManager.getExistingTicket(textChannel.getId()).ifPresent(ticket -> {
                if (user == null) return;

                ticketManager.removeUserFromTicket(event, ticket, user);
            });


            responder.respond();
        };
    }
}