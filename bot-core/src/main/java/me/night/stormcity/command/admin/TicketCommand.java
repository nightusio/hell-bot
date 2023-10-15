package me.night.stormcity.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.stormcity.config.MessageConfig;
import me.night.stormcity.config.TicketConfig;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TicketCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private final TicketConfig ticketConfig;

    @Inject
    public TicketCommand(final MessageConfig messageConfig, TicketConfig ticketConfig) {
        super("ticket", "Sends ticket embed");

        this.messageConfig = messageConfig;
        this.ticketConfig = ticketConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Collections.singletonList(
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to send embed to", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            Optional<SlashCommandInteractionOption> interationOptional = interaction.getArgumentByIndex(0);
            if (interationOptional.isEmpty()) return;
            Optional<ServerChannel> channelOptional = interationOptional.get().getChannelValue();
            if (channelOptional.isEmpty()) return;
            Channel channel = channelOptional.get();


            Optional<TextChannel> textChannel = channel.asTextChannel();

            if (textChannel.isEmpty()) {
                return;
            }

            ActionRow actionRow = ActionRow.of(SelectMenu.createStringMenu("menu", "Wybierz kategorie ticketu", 1, 1,
                    Arrays.asList(
                            SelectMenuOption.create("Inne", ticketConfig.inneButtonIDMenu, "Inne..."),
                            SelectMenuOption.create("Minecraft", ticketConfig.minecraftButtonIDMenu, "Sprawa dotyczaca minecrafta.")
                    )
            ));

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("StormCity | Ticket")
                    .setDescription("Bezpodstawne tworzenie ticketow bedzie wynagradzane banem")
                    .setFooter("StormCity | 2023")
                    .setColor(Color.GREEN)
                    .setTimestampToNow();

            textChannel.get().sendMessage(embed, actionRow);

            responder.setFlags(MessageFlag.EPHEMERAL);
            messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                    .put("channel", channel.getId())
                    .build());

            responder.respond();
        };
    }


}
