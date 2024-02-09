package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;

import me.night.helldev.config.MessageConfig;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonBuilder;
import org.javacord.api.entity.message.component.ButtonStyle;
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

public class VerifyCommand extends JavacordCommand {

    private final MessageConfig messageConfig;

    @Inject
    public VerifyCommand(final MessageConfig messageConfig) {
        super("verify", "Sends verify embed");

        this.messageConfig = messageConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Collections.singletonList(

                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to send embed to", false)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            Optional<ServerChannel> optionalServerChannel = interaction.getArgumentByIndex(1).flatMap(SlashCommandInteractionOption::getChannelValue);
            if (optionalServerChannel.isPresent()) {
                ServerChannel serverChannel = optionalServerChannel.get();

                handleVerifyCommand(interaction, responder, (TextChannel) serverChannel);
            } else {
                interaction.getChannel().ifPresent(channel -> handleVerifyCommand(interaction, responder, channel));
            }
        };
    }

    private void handleVerifyCommand(SlashCommandInteraction interaction, InteractionImmediateResponseBuilder responder, TextChannel textChannel) {

        Icon icon = interaction.getApi().getYourself().getAvatar();

        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(
                        "- **Witaj, zweryfikuj się, aby otrzymać dostęp do wszystkich kanałów. Weryfikując się, akceptujesz regulamin.**\n" +
                                "- Nie zapomnij przywitać się z innymi użytkownikami.")
                .setImage("https://cdn.discordapp.com/attachments/1193569983377195008/1196799942593892384/helldev-weryfikacja.png?ex=65b8f1cd&is=65a67ccd&hm=6381c05968604c5bf8535c0d1cc192d1b6d8dadaa0f1d6334441204a11d5c200&")
                .setAuthor("•  HELLDEV.PL - Twój serwer code!", "", icon)
                .setFooter("© 2024 HELLDEV.PL", icon)
                .setColor(Color.RED)
                .setTimestampToNow();

        Button button = new ButtonBuilder()
                .setStyle(ButtonStyle.SECONDARY)
                .setCustomId("verify")
                .setLabel("Zweryfikuj się")
                .setEmoji("\uD83D\uDC4B")
                .build();

        ActionRow actionRow = ActionRow.of(button);
        textChannel.sendMessage(embed, actionRow);

        responder.setFlags(MessageFlag.EPHEMERAL);
        messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                .put("channel", textChannel.getId())
                .build());

        responder.respond();
    }
}