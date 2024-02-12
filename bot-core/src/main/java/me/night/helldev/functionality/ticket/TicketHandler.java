package me.night.helldev.functionality.ticket;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import me.night.helldev.functionality.ticket.utility.TicketCategoryUtility;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
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
    private final TicketCategoryManager ticketCategoryManager;
    private final TicketConfig ticketConfig;

    //todo dodawanie osob z dana ranga 

    public TextChannel createNewTicket(User user, String category, Server server) {
        int ticketId = newTicketId();

        Ticket ticket = ticketManager.createTicket(ticketId, user.getId(), category, server);

        TicketCategory ticketCategory = ticketCategoryManager.getTicketCategoryByTicket(ticket);
        ChannelCategory channelCategory = TicketCategoryUtility.getServerCategoryById(server, ticketCategory.getCategoryId());

        Role everyoneRole = server.getEveryoneRole();

        Permissions everyonePermissions = new PermissionsBuilder()
                .setDenied(PermissionType.VIEW_CHANNEL)
                .build();

        Permissions permissions = new PermissionsBuilder()
                .setAllowed(PermissionType.VIEW_CHANNEL)
                .setAllowed(PermissionType.SEND_MESSAGES)
                .setAllowed(PermissionType.READ_MESSAGE_HISTORY)
                .build();

        String channelName = ticketCategory.getTicketName() + user.getDiscriminatedName();
        ServerTextChannel channel = server.createTextChannelBuilder()
                .setName(channelName)
                .addPermissionOverwrite(user, permissions)
                .addPermissionOverwrite(everyoneRole, everyonePermissions)
                .setCategory(channelCategory)
                .create()
                .join();

        channel.sendMessage(user.getMentionTag())
                .thenAccept(message -> message.delete("."));
        Message message = ticketMessage(channel, ticketCategory.getButtonClose());

        ticketManager.setChannelId(ticket, channel.getId());
        ticketManager.setMessageId(ticket, message.getId());

        return channel;
    }

    public void closeTicket(Server server, User user, ButtonClickEvent event, Ticket ticket, String buttonDeleteTicket) {
        Optional<ServerTextChannel> channelOptional = server.getTextChannelById(ticket.getChannelId());

        if (channelOptional.isEmpty()) return;

        ServerTextChannel textChannel = channelOptional.get();

        textChannel.createUpdater().removePermissionOverwrite(user).update();

        for (Long usersId : ticket.getAddedUsers()) {
            event.getApi().getUserById(usersId).thenAccept(user1 -> {
                textChannel.createUpdater().removePermissionOverwrite(user1).update();
            });
        }

        EmbedBuilder adminPanelEmbed = new EmbedBuilder()
                .setDescription("```       \uD83D\uDCE7 × HellDev.pl - Admin Panel       ```\n" +
                        "\n" +
                        "> Kliknij przycisk ponizej aby usunac ticket\n")
                .setFooter("© 2024 helldev.pl •")
                .setColor(Color.RED)
                .setTimestampToNow();

        Button deleteButton = Button.danger(buttonDeleteTicket, "Usun Ticket", "\uD83D\uDDD1️");
        ActionRow adminPanelRow = ActionRow.of(deleteButton);

        event.getInteraction().createImmediateResponder()
                .addEmbed(adminPanelEmbed)
                .addComponents(adminPanelRow)
                .respond();
    }

    public void deleteTicket(Server server, Ticket ticket) {
        if (ticket == null) return;

        Optional<ServerTextChannel> channelOptional = server.getTextChannelById(ticket.getChannelId());

        if (channelOptional.isEmpty()) return;

        ServerTextChannel textChannel = channelOptional.get();

        textChannel.delete();
        ticketManager.deleteTicket(textChannel.getId());
    }

    public Message ticketMessage(TextChannel textChannel, String buttonTicketClose) {
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription("```       " +
                        "\uD83D\uDCE7 × HellDev.pl - Ticket       ```\n" +
                        "\n" +
                        "> Administracja zostala poinformowana\n" +
                        "> **Dostaniesz odpowiedz do 48h!**\n")
                .setFooter("© 2024 helldev.pl •")
                .setColor(Color.RED)
                .setTimestampToNow();

        Button ticketButton = Button.primary(buttonTicketClose, "Zamknij Ticket", "\uD83D\uDCE5");
        ActionRow actionRow = ActionRow.of(ticketButton);

        return textChannel.sendMessage(embed, actionRow).join();
    }

    private int newTicketId() {
        int maxId = 0;
        for (Ticket existingTickets : ticketConfig.tickets) {
            if (existingTickets.getId() > maxId) {
                maxId = existingTickets.getId();
            }
        }
        return maxId + 1;
    }

}
