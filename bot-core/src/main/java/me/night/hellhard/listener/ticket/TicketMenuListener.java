package me.night.hellhard.listener.ticket;

import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.night.hellhard.config.BotConfig;
import me.night.hellhard.config.TicketConfig;
import me.night.hellhard.ticket.TicketManager;
import me.night.hellhard.ticket.TicketType;
import me.night.hellhard.ticket.impl.TicketHandler;
import org.javacord.api.entity.channel.ChannelCategory;
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

    private final BotConfig botConfig;
    private final TicketManager ticketManager;
    private final TicketConfig ticketConfig;
    private final TicketHandler ticketHandler;

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        User user = event.getInteraction().getUser();
        Optional<Server> serverOptional = event.getInteraction().getServer();

        if (serverOptional.isEmpty()) return;


        Optional<ChannelCategory> categoryOptional = serverOptional.get().getChannelCategoryById(botConfig.ticketCategoryID);
        if (categoryOptional.isEmpty()) return;

        ChannelCategory category = categoryOptional.get();
        Server server = serverOptional.get();

        if (event.getSelectMenuInteraction().getCustomId().equalsIgnoreCase("menu")) {
            List<SelectMenuOption> chosenOptions = event.getSelectMenuInteraction().getChosenOptions();
            if (chosenOptions.isEmpty()) return;

            SelectMenuOption selectedOption = chosenOptions.get(0);

            if (selectedOption.getValue().equals(ticketConfig.inneButtonIDMenu)) {
                String userChannelID = ticketManager.getUserTicketByTicketType(user.getId(), TicketType.INNE);
                handleTicketButton(event, server, user, userChannelID, category, ticketConfig.inneButtonClose, ticketConfig.inneTicketName, TicketType.INNE);
                event.getInteraction().createImmediateResponder().respond();
            }

            if (selectedOption.getValue().equals(ticketConfig.minecraftButtonIDMenu)) {
                String userChannelID = ticketManager.getUserTicketByTicketType(user.getId(), TicketType.MINECRAFT);
                handleTicketButton(event, server, user, userChannelID, category, ticketConfig.minecraftButtonClose, ticketConfig.minecraftTicketName, TicketType.MINECRAFT);
                event.getInteraction().createImmediateResponder().respond();
            }
        }
    }


    private void handleTicketButton(SelectMenuChooseEvent event, @NonNull Server server, @NonNull User user, String userChannelID, @NonNull ChannelCategory category, @NonNull String buttonClose, String ticketName, TicketType ticketType) {
        if (userChannelID != null) {
            server.getTextChannelById(userChannelID).ifPresent(existingChannel -> {
                String responseContent = "Masz już otwarty ticket: " + existingChannel.getMentionTag();
                respondWithEphemeralMessage(event, responseContent);
            });
        } else {
            ticketHandler.createNewTicketChannel(server, user, category, buttonClose, ticketName, ticketType);
        }
    }

    private void respondWithEphemeralMessage(SelectMenuChooseEvent event, String content) {
        event.getInteraction().createImmediateResponder()
                .setContent(content)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

}
