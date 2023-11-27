package me.night.helldev.functionality.ticket;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import me.night.helldev.functionality.ticket.exception.TicketException;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.util.HashSet;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TicketManager {

    private final TicketConfig ticketConfig;
    private final TicketCategoryManager ticketCategoryManager;

    public Ticket createTicket(int id, long userID, String category, Server server) {
        Ticket ticket = new Ticket(id, userID, category, server.getId(), new HashSet<>(), 0L, 0L);
        ticketConfig.tickets.add(ticket);
        ticketConfig.save();
        return ticket;
    }

    /**
     * Gets an existing ticket based on its channel ID.
     *
     * @param channelID The channel ID of the ticket to retrieve.
     * @return The existing ticket or null if not found.
     */
    public Ticket getExistingTicket(long channelID) {
        return ticketConfig.tickets.stream()
                .filter(ticket -> ticket.getChannelId() == channelID)
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a ticket based on its channel ID.
     *
     * @param channelID The channel ID of the ticket to be deleted.
     */
    public void deleteTicket(long channelID) {
        Optional<Ticket> ticketToRemove = ticketConfig.tickets.stream()
                .filter(ticket -> ticket.getChannelId() == channelID)
                .findFirst();

        ticketToRemove.ifPresent(ticket -> ticketConfig.tickets.remove(ticket));
        ticketConfig.save();
    }

    /**
     * Gets the channel ID of an existing ticket for a specific user and ticket type based on their user ID.
     *
     * @param userID     The user ID for which to retrieve tickets.
     * @param categoryId The ID of the TicketCategory to filter by.
     * @return The channel ID of the matching ticket or 0 if not found.
     */
    public long getUserTicketByCategory(long userID, String categoryId) {
        Optional<Ticket> matchingTicket = ticketConfig.tickets.stream()
                .filter(ticket -> ticket.getUserId() == userID &&
                        isTicketCategoryMatch(ticket.getCategory(), categoryId))
                .findFirst();

        return matchingTicket.map(Ticket::getChannelId).orElse(0L);
    }

    /**
     * Checks if the given category matches the specified TicketCategory ID.
     *
     * @param category   The category of the ticket.
     * @param categoryId The ID of the TicketCategory to match.
     * @return True if the category matches the TicketCategory ID, false otherwise.
     */
    private boolean isTicketCategoryMatch(String category, String categoryId) {
        try {
            TicketCategory ticketCategory = ticketCategoryManager.getTicketCategory(categoryId);
            return ticketCategory.getId().equalsIgnoreCase(category)
                    || ticketCategory.getButtonIDMenu().equalsIgnoreCase(category)
                    || ticketCategory.getButtonClose().equalsIgnoreCase(category)
                    || ticketCategory.getButtonConfirmClose().equalsIgnoreCase(category)
                    || ticketCategory.getButtonDelete().equalsIgnoreCase(category)
                    || ticketCategory.getTicketName().equalsIgnoreCase(category);
        } catch (TicketException e) {
            return false;
        }
    }

    public void addUserToTicket(SlashCommandCreateEvent event, Ticket ticket, User user) {
        if (ticket.getAddedUsers().contains(user.getId())) {
            MessageUtility.respondWithEphemeralMessage(event, "```[ ❌ ] Ten użytkownik jest już dodany do tego ticketa!```");
            return;
        }

        ticket.getAddedUsers().add(user.getId());
        ticketConfig.save();

        event.getApi().getServerTextChannelById(ticket.getChannelId())
                .ifPresent(textChannel -> {
                    Permissions userPermissions = new PermissionsBuilder()
                            .setAllowed(PermissionType.VIEW_CHANNEL, PermissionType.SEND_MESSAGES, PermissionType.READ_MESSAGE_HISTORY)
                            .build();

                    textChannel.createUpdater().addPermissionOverwrite(user, userPermissions).update();
                });

        Optional<TextChannel> optionalChannel = event.getApi().getTextChannelById(ticket.getChannelId());

        if (optionalChannel.isEmpty()) return;

        TextChannel channel = optionalChannel.get();

        channel.sendMessage("Użytkownik " + user.getMentionTag() + " został dodany do ticketa.");
        MessageUtility.respondWithEphemeralMessage(event, "```[ ✅ ] Pomyślnie dodałeś " + user.getName() + "!```");
    }


    public void removeUserFromTicket(SlashCommandCreateEvent event, Ticket ticket, User user) {
        if (!ticket.getAddedUsers().contains(user.getId())) {
            MessageUtility.respondWithEphemeralMessage(event, "```[ ❌ ] Ten uzytkownik nie jest dodany do ticketa!```");
            return;
        }

        ticket.getAddedUsers().remove(user.getId());
        ticketConfig.save();

        event.getApi().getServerTextChannelById(ticket.getChannelId())
                .ifPresent(textChannel -> {
                    Permissions userPermissions = new PermissionsBuilder()
                            .setDenied(PermissionType.VIEW_CHANNEL, PermissionType.SEND_MESSAGES, PermissionType.READ_MESSAGE_HISTORY)
                            .build();

                    textChannel.createUpdater().addPermissionOverwrite(user, userPermissions).update();
                });

        Optional<TextChannel> optionalChannel = event.getApi().getTextChannelById(ticket.getChannelId());

        if (optionalChannel.isEmpty()) return;

        TextChannel channel = optionalChannel.get();

        channel.sendMessage("Uzytkownik " + user.getMentionTag() + " zostal usuniety z tego ticketa.");
        MessageUtility.respondWithEphemeralMessage(event, "```[ ✅ ] Pomyslnie usunales " + user.getName() + "!```");
    }


    public void setMessageId(Ticket ticket, long messageId) {
        ticket.setMessageId(messageId);
        ticketConfig.save();
    }

    public void setChannelId(Ticket ticket, long channelId) {
        ticket.setChannelId(channelId);
        ticketConfig.save();
    }
}
