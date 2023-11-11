package me.night.helldev.functionality.ticket;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.shared.SharedType;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TicketManager {

    private final TicketConfig ticketConfig;

    /**
     * Creates a new ticket and adds it to the ticket list.
     *
     * @param ticket The ticket to be created.
     */
    public void createNewTicket(Ticket ticket) {
        ticketConfig.tickets.add(ticket);
        ticketConfig.save();
    }

    /**
     * Gets an existing ticket based on its channel ID.
     *
     * @param channelID The channel ID of the ticket to retrieve as a String.
     * @return The existing ticket or null if not found.
     */
    public Ticket getExistingTicket(String channelID) {
        return ticketConfig.tickets.stream()
                .filter(ticket -> ticket.getChannelID().equals(channelID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a ticket based on its channel ID.
     *
     * @param channelID The channel ID of the ticket to be deleted.
     */
    public void deleteTicket(String channelID) {
        Optional<Ticket> ticketToRemove = ticketConfig.tickets.stream()
                .filter(ticket -> ticket.getChannelID().equals(channelID))
                .findFirst();

        ticketToRemove.ifPresent(ticket -> ticketConfig.tickets.remove(ticket));
        ticketConfig.save();
    }


    /**
     * Gets the channel ID of an existing ticket for a specific user and ticket type based on their user ID.
     *
     * @param userID     The user ID for which to retrieve tickets.
     * @param sharedType The ticket type to filter by.
     * @return The channel ID of the matching ticket as a String or null if not found.
     */
    public String getUserTicketByTicketType(long userID, SharedType sharedType) {
        Optional<Ticket> matchingTicket = ticketConfig.tickets.stream()
                .filter(ticket -> ticket.getUserID() == userID && ticket.getSharedType() == sharedType)
                .findFirst();

        return matchingTicket.map(Ticket::getChannelID).orElse(null);
    }

}
