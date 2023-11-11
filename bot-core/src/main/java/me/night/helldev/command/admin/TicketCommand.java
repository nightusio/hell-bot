package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.ticket.TicketConfig;
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
                            SelectMenuOption.create("Usluga", ticketConfig.uslugaButtonIDMenu, "Chce zakupic usluge.", "\uD83D\uDCB8"),
                            SelectMenuOption.create("Pomoc", ticketConfig.pomocButtonIDMenu, "Potrzebuje pomocy.", "\uD83D\uDD27"),
                            SelectMenuOption.create("Partnerstwo", ticketConfig.partnerstwoButtonIDMenu, "Chce zawrzec partnerstwo.", "\uD83E\uDD1D"),
                            SelectMenuOption.create("Inne", ticketConfig.inneButtonIDMenu, "Inne...", "✨")
                            )
            ));

            EmbedBuilder embed = new EmbedBuilder()

                    .setDescription("```       \uD83D\uDCDC × HellDev.pl - Ticket Info ```\n" +
                            "\n" +
                            "> **Potrzebujesz pomocy?**\n" +
                            "> **Chcesz nawiązać partnerstwo?**\n" +
                            "> **A może chcesz kupić usługę?**\n" +
                            "\n" +
                            "> Rozwiń liste poniżej i wybierz odpowiedni kanał do otwarcia.\n" +
                            "> Bezpodstawne tworzenie ticketow bedzie skutkowac banem!")
                    .setFooter("© 2023 helldev.pl", interaction.getApi().getYourself().getAvatar())
                    .setAuthor("helldev.pl - Stwórz ticket", "", interaction.getApi().getYourself().getAvatar())
                    .setImage("https://media.discordapp.net/attachments/1166458384426483764/1171555808266428458/helldev-ticket.png?ex=655d1b5f&is=654aa65f&hm=4f764fcc8851dee063780bd4d980b03b76d353515d9f77d21925f61299a6d67d&=")
                    .setColor(Color.RED)
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
