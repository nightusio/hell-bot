package me.night.helldev.functionality.ticket.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.ticket.Ticket;
import me.night.helldev.functionality.ticket.TicketHandler;
import me.night.helldev.functionality.ticket.TicketManager;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import me.night.helldev.functionality.ticket.utility.TicketButtonEditUtility;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TicketButtonListener implements ButtonClickListener {

    private final TicketManager ticketManager;
    private final TicketHandler ticketHandler;
    private final TicketCategoryManager ticketCategoryManager;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        User user = event.getInteraction().getUser();
        Optional<Server> serverOptional = event.getInteraction().getServer();

        if (serverOptional.isEmpty()) return;

        Server server = serverOptional.get();

        Optional<TextChannel> optionalChannel = event.getInteraction().getChannel();

        if (optionalChannel.isEmpty()) return;

        Channel channel = optionalChannel.get();

        Ticket ticket = ticketManager.getExistingTicket(channel.getId());
        TextChannel textChannel;


        if(ticket == null) return;

        for (TicketCategory ticketCategory : ticketCategoryManager.getTicketCategories()) {

            if (event.getButtonInteraction().getCustomId().equals(ticketCategory.getButtonClose())) {
                TicketButtonEditUtility.editActionRowsCloseTicket(event.getApi(), ticket, ticketCategoryManager);
                event.getInteraction().createImmediateResponder().respond();
                break;

            } else if (event.getButtonInteraction().getCustomId().equals(ticketCategory.getButtonConfirmClose())) {
                textChannel = event.getApi().getTextChannelById(ticket.getChannelId()).get();
                textChannel.sendMessage("```Ticket został zamkniety przez: " + event.getInteraction().getUser().getName() + "```");

                ticketHandler.closeTicket(server, user, event, ticket, ticketCategory.getButtonDelete());
                TicketButtonEditUtility.editActionRowsClosedTicket(event.getApi(), ticket);
                break;

            } else if (event.getButtonInteraction().getCustomId().equals(ticketCategory.getButtonDelete())) {
                ticketHandler.deleteTicket(server, ticket);
                break;

            }

        }
    }

}
