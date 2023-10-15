package me.night.stormcity.listener.ticket;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.stormcity.config.BotConfig;
import me.night.stormcity.config.TicketConfig;
import me.night.stormcity.ticket.Ticket;
import me.night.stormcity.ticket.TicketManager;
import me.night.stormcity.ticket.impl.TicketHandler;
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

        Optional<ChannelCategory> categoryOptional = serverOptional.get().getChannelCategoryById(botConfig.ticketCategoryID);
        if (categoryOptional.isEmpty()) return;

        Server server = serverOptional.get();

        Optional<TextChannel> optionalChannel = event.getInteraction().getChannel();

        if (optionalChannel.isEmpty()) return;

        Channel channel = optionalChannel.get();

        Ticket ticket = ticketManager.getExistingTicket(channel.getIdAsString());

       if (event.getButtonInteraction().getCustomId().equals(ticketConfig.inneButtonClose)) {
            ticketHandler.confirmTicketClose(event, ticketConfig.inneButtonConfirmClose, ticket.getChannelID());
        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.inneButtonConfirmClose)) {
            ticketHandler.closeTicket(server, user, event, ticket, ticketConfig.inneButtonDelete);
        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.inneButtonDelete)) {
            ticketHandler.deleteTicket(server, ticket);
        }

        if (event.getButtonInteraction().getCustomId().equals(ticketConfig.minecraftButtonClose)) {
            ticketHandler.confirmTicketClose(event, ticketConfig.minecraftButtonConfirmClose, ticket.getChannelID());
        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.minecraftButtonConfirmClose)) {
            ticketHandler.closeTicket(server, user, event, ticket, ticketConfig.minecraftButtonDelete);
        } else if (event.getButtonInteraction().getCustomId().equals(ticketConfig.minecraftButtonDelete)) {
            ticketHandler.deleteTicket(server, ticket);
        }
    }

}
