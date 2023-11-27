package me.night.helldev.command.admin.ticket;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import me.night.helldev.functionality.ticket.exception.TicketException;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TicketCategoryDeleteCommand  extends JavacordCommand {

    private final TicketCategoryManager ticketCategoryManager;

    @Inject
    public TicketCategoryDeleteCommand(final TicketCategoryManager ticketCategoryManager) {
        super("ticketcategorydelete", "Deletes ticket category");

        this.ticketCategoryManager = ticketCategoryManager;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Collections.singletonList(

                SlashCommandOption.create(SlashCommandOptionType.STRING, "id", "Id name of ticket category to delete.", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            String id = interaction.getArgumentByIndex(0)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse(null);

            if (id == null) return;


            try {
                TicketCategory ticketCategory = ticketCategoryManager.getTicketCategory(id);
                ticketCategoryManager.removeTicketCategory(ticketCategory);
                responder.setContent("```[ ✅ ] Kategoria " + id + " zostala pomyslnie usunieta!```");
            } catch (OkaeriException | NullPointerException | TicketException exception) {

                if (exception instanceof TicketException) {
                    MessageUtility.respondWithEphemeralMessage(event, "```[ ❌ ] Kategoria " + id +  " nie istnieje!```");
                }
            }

            responder.respond();
        };
    }
}