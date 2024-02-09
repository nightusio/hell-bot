package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.MessageConfig;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
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

public class ClearCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private static final int MAX_MESSAGES_TO_DELETE = 150;

    @Inject
    public ClearCommand(final MessageConfig messageConfig) {
        super("clear", "Clears specified amount of messages");

        this.messageConfig = messageConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Arrays.asList(
                SlashCommandOption.create(SlashCommandOptionType.STRING, "amount", "Amount of messages to delete", true),
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to delete messages in", false)
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

                handleClearCommand(interaction, responder, (TextChannel) serverChannel);
            } else {
                interaction.getChannel().ifPresent(channel -> {
                    handleClearCommand(interaction, responder, channel);
                });
            }
        };
    }

    private void handleClearCommand(SlashCommandInteraction interaction, InteractionImmediateResponseBuilder responder, TextChannel textChannel) {
        int requestedNumber;
        try {
            requestedNumber = Integer.parseInt(interaction.getArgumentByIndex(0).get().getStringRepresentationValue().get());
            if (requestedNumber > MAX_MESSAGES_TO_DELETE) {
                responder.setFlags(MessageFlag.EPHEMERAL);
                this.messageConfig.maxLimitExceeded.send(interaction.getUser());
                responder.respond();
                return;
            }
        } catch (NumberFormatException exception) {
            responder.setFlags(MessageFlag.EPHEMERAL);
            this.messageConfig.notNumber.send(interaction.getUser());
            responder.respond();
            return;
        }

        textChannel.getMessages(requestedNumber).thenAccept(messages -> {
            int actualNumber = messages.size();
            textChannel.bulkDelete(messages);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Usunięto " + actualNumber + " wiadomości!")
                    .setDescription("→ Usunąłeś " + actualNumber + " wiadomości na kanale <#" + textChannel.getIdAsString() + ">")
                    .setFooter("© 2023 HellDev -", interaction.getApi().getYourself().getAvatar())
                    .setAuthor("HellDev - Clear", "", interaction.getApi().getYourself().getAvatar())
                    .setColor(Color.GREEN)
                    .setTimestampToNow();

            responder.setFlags(MessageFlag.EPHEMERAL);
            textChannel.sendMessage(embed);

            responder.respond();
        });
    }
}
