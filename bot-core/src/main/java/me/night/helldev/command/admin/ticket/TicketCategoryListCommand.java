package me.night.helldev.command.admin.ticket;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.List;

public class TicketCategoryListCommand  extends JavacordCommand {

    private final TicketCategoryManager ticketCategoryManager;

    @Inject
    public TicketCategoryListCommand(final TicketCategoryManager ticketCategoryManager) {
        super("ticketcategorylist", "Gets all categories");

        this.ticketCategoryManager = ticketCategoryManager;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            List<TicketCategory> ticketCategoryList = ticketCategoryManager.getTicketCategories();

            responder.setFlags(MessageFlag.EPHEMERAL);

            StringBuilder contentBuilder = new StringBuilder("Lista kategorii:\n");

            for (int i = 0; i < ticketCategoryList.size(); i++) {
                TicketCategory category = ticketCategoryList.get(i);
                contentBuilder.append(i + 1).append(". ").append(category.getId()).append("\n");
            }

            responder.setContent(contentBuilder.toString());

            responder.respond();
        };
    }

}