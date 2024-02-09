package me.night.helldev.functionality.ticket.listener;

import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.ticket.TicketHandler;
import me.night.helldev.functionality.ticket.TicketManager;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import me.night.helldev.functionality.ticket.exception.TicketError;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TicketMenuListener implements SelectMenuChooseListener {

    private final TicketManager ticketManager;
    private final TicketHandler ticketHandler;
    private final TicketCategoryManager ticketCategoryManager;
    private final MessageConfig messageConfig;

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        User user = event.getInteraction().getUser();
        Optional<Server> serverOptional = event.getInteraction().getServer();

        if (serverOptional.isEmpty()) return;

        Server server = serverOptional.get();

        if (event.getSelectMenuInteraction().getCustomId().equalsIgnoreCase("menu")) {
            List<SelectMenuOption> chosenOptions = event.getSelectMenuInteraction().getChosenOptions();
            if (chosenOptions.isEmpty()) return;

            SelectMenuOption selectedOption = chosenOptions.get(0);
            String selectedStripped = selectedOption.getValue().replaceAll("ticket_", "");

            long userChannelId = ticketManager.getUserTicketByCategory(user.getId(), selectedOption.getValue());

            for (TicketCategory ticketCategory : ticketCategoryManager.getTicketCategories()) {
                if (selectedOption.getValue().equals(ticketCategory.getButtonIDMenu())) {
                    handleTicketButton(event, server, userChannelId, user, selectedStripped);
                    event.getInteraction().createImmediateResponder().respond();
                    break;
                }
            }
        }
    }


    private void handleTicketButton(SelectMenuChooseEvent event, @NonNull Server server, long userChannelId, @NonNull User user, String category) {
        if (userChannelId != 0L) {
            server.getTextChannelById(userChannelId).ifPresent(existingChannel -> {
                String responseContent = "Masz już otwarty ticket: " + existingChannel.getMentionTag();
                MessageUtility.respondWithEphemeralMessage(event, responseContent);
            });
        } else {
            try {
               TextChannel textChannel = ticketHandler.createNewTicket(user, category, server);
               event.getInteraction().createImmediateResponder().setFlags(MessageFlag.EPHEMERAL);
               messageConfig.ticketCreated.applyToResponder(event.getInteraction().createImmediateResponder(), new MapBuilder<String, Object>()
                       .put("channel", textChannel.getId())
                       .build());
            } catch (TicketError e) {
                MessageUtility.respondWithEphemeralMessage(event, e.getMessage());
            }
        }
    }
}
