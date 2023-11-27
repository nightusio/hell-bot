package me.night.helldev.functionality.ticket.utility;

import lombok.experimental.UtilityClass;
import me.night.helldev.functionality.ticket.Ticket;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageUpdater;
import org.javacord.api.entity.message.component.Button;

import java.util.Optional;

@UtilityClass
public class TicketButtonEditUtility {

    public void editActionRowsCloseTicket(DiscordApi api, Ticket ticket, TicketCategoryManager ticketCategoryManager) {
        long textChannelId = ticket.getChannelId();
        long messageId = ticket.getMessageId();

        Optional<TextChannel> textChannelOptional = api.getTextChannelById(textChannelId);

        if (textChannelOptional.isPresent()) {
            TextChannel textChannel = textChannelOptional.get();

            Message message = MessageUtility.getMessageInTextChannel(messageId, textChannel);

            if (message != null) {

                TicketCategory ticketCategory = ticketCategoryManager.getTicketCategoryByTicket(ticket);

                Button confirm = Button.success(ticketCategory.getButtonConfirmClose(), "Na pewno chcesz zamknac ticket?", "\uD83D\uDCE5");

                MessageUpdater messageUpdater = new MessageUpdater(message)
                        .removeAllComponents()
                        .addActionRow(confirm);

                messageUpdater.applyChanges();
            }
        }
    }

    public void editActionRowsClosedTicket(DiscordApi api, Ticket ticket) {
        long textChannelId = ticket.getChannelId();
        long messageId = ticket.getMessageId();

        Optional<TextChannel> textChannelOptional = api.getTextChannelById(textChannelId);

        if (textChannelOptional.isPresent()) {
            TextChannel textChannel = textChannelOptional.get();

            Message message = MessageUtility.getMessageInTextChannel(messageId, textChannel);

            if (message != null) {
                Button closed = Button.secondary("closed", "Ticket zostal zamkniety" , "\uD83D\uDD12", true);

                MessageUpdater messageUpdater = new MessageUpdater(message)
                        .removeAllComponents()
                        .addActionRow(closed);

                messageUpdater.applyChanges();
            }
        }
    }

}
