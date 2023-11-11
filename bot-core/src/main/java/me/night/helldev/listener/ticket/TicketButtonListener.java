package me.night.helldev.listener.ticket;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.config.BotConfig;
import me.night.helldev.functionality.ticket.TicketConfig;
import me.night.helldev.functionality.ticket.Ticket;
import me.night.helldev.functionality.ticket.TicketManager;
import me.night.helldev.functionality.ticket.impl.TicketHandler;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TicketButtonListener implements ButtonClickListener {

    private final BotConfig botConfig;
    private final TicketManager ticketManager;
    private final TicketConfig ticketConfig;
    private final TicketHandler ticketHandler;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        User user = event.getInteraction().getUser();
        Optional<Server> serverOptional = event.getInteraction().getServer();

        if (serverOptional.isEmpty()) return;

        Optional<ChannelCategory> categoryOptional = serverOptional.get().getChannelCategoryById(ticketConfig.ticketCategoryId);
        if (categoryOptional.isEmpty()) return;

        Server server = serverOptional.get();

        Optional<TextChannel> optionalChannel = event.getInteraction().getChannel();

        if (optionalChannel.isEmpty()) return;

        Channel channel = optionalChannel.get();

        Ticket ticket = ticketManager.getExistingTicket(channel.getIdAsString());
        TextChannel textChannel;


        if(ticket == null) return;

       if (event.getButtonInteraction().getCustomId().equals(ticketConfig.inneButtonClose)) {
            ticketHandler.confirmTicketClose(event, ticketConfig.inneButtonConfirmClose, ticket.getChannelID());
        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.inneButtonConfirmClose)) {
            ticketHandler.closeTicket(server, user, event, ticket, ticketConfig.inneButtonDelete);
           textChannel = event.getApi().getTextChannelById(ticket.getChannelID()).get();
           textChannel.sendMessage("```Ticket został zamkniety przez: " + event.getInteraction().getUser().getName() + "```");

       } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.inneButtonDelete)) {
            ticketHandler.deleteTicket(server, ticket);
        }

        if (event.getButtonInteraction().getCustomId().equals(ticketConfig.uslugaButtonClose)) {
            ticketHandler.confirmTicketClose(event, ticketConfig.uslugaButtonConfirmClose, ticket.getChannelID());

        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.uslugaButtonConfirmClose)) {
            ticketHandler.closeTicket(server, user, event, ticket, ticketConfig.uslugaButtonDelete);
            textChannel = event.getApi().getTextChannelById(ticket.getChannelID()).get();
            textChannel.sendMessage("```Ticket został zamkniety przez: " + event.getInteraction().getUser().getName() + "```");

        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.uslugaButtonDelete)) {
            ticketHandler.deleteTicket(server, ticket);
        }

        if (event.getButtonInteraction().getCustomId().equals(ticketConfig.partnerstwoButtonClose)) {
            ticketHandler.confirmTicketClose(event, ticketConfig.partnerstwoButtonConfirmClose, ticket.getChannelID());

        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.partnerstwoButtonConfirmClose)) {
            ticketHandler.closeTicket(server, user, event, ticket, ticketConfig.partnerstwoButtonDelete);
            textChannel = event.getApi().getTextChannelById(ticket.getChannelID()).get();
            textChannel.sendMessage("```Ticket został zamkniety przez: " + event.getInteraction().getUser().getName() + "```");

        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.partnerstwoButtonDelete)) {
            ticketHandler.deleteTicket(server, ticket);
        }

        if (event.getButtonInteraction().getCustomId().equals(ticketConfig.pomocButtonClose)) {
            ticketHandler.confirmTicketClose(event, ticketConfig.pomocButtonConfirmClose, ticket.getChannelID());
        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.pomocButtonConfirmClose)) {
            ticketHandler.closeTicket(server, user, event, ticket, ticketConfig.pomocButtonDelete);
            textChannel = event.getApi().getTextChannelById(ticket.getChannelID()).get();
            textChannel.sendMessage("```Ticket został zamkniety przez: " + event.getInteraction().getUser().getName() + "```");

        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.pomocButtonDelete)) {
            ticketHandler.deleteTicket(server, ticket);
        }
    }

}
