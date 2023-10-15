package me.night.hellhard.ticket.impl;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.hellhard.ticket.Ticket;
import me.night.hellhard.ticket.TicketManager;
import me.night.hellhard.ticket.TicketType;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;

import java.awt.*;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TicketHandler {

    private final TicketManager ticketManager;

    public void createNewTicketChannel(Server server, User user, ChannelCategory category, String buttonTicketClose, String ticketName, TicketType ticketType) {
        Role everyoneRole = server.getEveryoneRole();

        Permissions everyonePermissions = new PermissionsBuilder()
                .setDenied(PermissionType.VIEW_CHANNEL)
                .build();

        Permissions permissions = new PermissionsBuilder()
                .setAllowed(PermissionType.VIEW_CHANNEL)
                .setAllowed(PermissionType.SEND_MESSAGES)
                .setAllowed(PermissionType.READ_MESSAGE_HISTORY)
                .build();

        String channelName = ticketName + user.getDiscriminatedName();
        ServerTextChannel channel = server.createTextChannelBuilder()
                .setName(channelName)
                .addPermissionOverwrite(user, permissions)
                .addPermissionOverwrite(everyoneRole, everyonePermissions)
                .setCategory(category)
                .create()
                .join();

        Ticket ticketToCreate = new Ticket(user.getId(), ticketType, channel.getIdAsString());
        ticketManager.createNewTicket(ticketToCreate);

        channel.sendMessage(user.getMentionTag());
        ticketMessage(channel, buttonTicketClose);
    }


    public void confirmTicketClose(ButtonClickEvent event, String buttonConfirmCloseTicket, String channelId) {
        if (event.getInteraction().getChannel().map(TextChannel::getIdAsString).filter(channelId::equals).isPresent()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("HellHard | Ticket")
                    .setDescription("Napewno chcesz zamknąć ticket?")
                    .setFooter("HellHard | 2023")
                    .setColor(Color.GREEN)
                    .setTimestampToNow();

            Button closeButton = Button.primary(buttonConfirmCloseTicket, "Zamknij");
            ActionRow actionRow = ActionRow.of(closeButton);

            event.getInteraction().createImmediateResponder()
                    .addEmbed(embed)
                    .addComponents(actionRow)
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }

    public void closeTicket(Server server, User user, ButtonClickEvent event, Ticket ticket, String buttonDeleteTicket) {
        Optional<ServerTextChannel> channelOptional = server.getTextChannelById(ticket.getChannelID());

        if (channelOptional.isEmpty()) return;

        ServerTextChannel textChannel = channelOptional.get();

        textChannel.asServerTextChannel().ifPresent(serverTextChannel -> serverTextChannel.getOverwrittenPermissions(user).toBuilder()
                .setDenied(PermissionType.VIEW_CHANNEL)
                .build());

        EmbedBuilder adminPanelEmbed = new EmbedBuilder()
                .setTitle("HellHard | Ticket")
                .setDescription("Panel Administracyjny")
                .setFooter("HellHard | 2023")
                .setColor(Color.GREEN)
                .setTimestampToNow();

        Button deleteButton = Button.primary(buttonDeleteTicket, "Usun Ticket");
        ActionRow adminPanelRow = ActionRow.of(deleteButton);

        event.getInteraction().createImmediateResponder()
                .addEmbed(adminPanelEmbed)
                .addComponents(adminPanelRow)
                .respond();
    }

    public void deleteTicket(Server server, Ticket ticket) {
        if (ticket == null) return;

        Optional<ServerTextChannel> channelOptional = server.getTextChannelById(ticket.getChannelID());

        if (channelOptional.isEmpty()) return;

        ServerTextChannel textChannel = channelOptional.get();

        textChannel.delete();
        ticketManager.deleteTicket(textChannel.getIdAsString());
    }

    public void ticketMessage(TextChannel textChannel, String buttonTicketClose) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("HellHard | Ticket")
                .setDescription("Opisz swoj problem.")
                .addField("", "")
                .addField("", "Aby zamknac ticket kliknij przycisk ponizej")
                .setFooter("HellHard | 2023")
                .setColor(Color.GREEN)
                .setTimestampToNow();

        Button ticketButton = Button.primary(buttonTicketClose, "Zamknij Ticket");
        ActionRow actionRow = ActionRow.of(ticketButton);

        textChannel.sendMessage(embed, actionRow).join();
    }
}
