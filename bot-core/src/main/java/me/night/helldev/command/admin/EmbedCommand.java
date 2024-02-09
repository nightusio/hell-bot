package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.MessageConfig;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EmbedCommand extends JavacordCommand {

    private final MessageConfig messageConfig;

    @Inject
    public EmbedCommand(final MessageConfig messageConfig) {
        super("embed", "Sends custom embed");

        this.messageConfig = messageConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Arrays.asList(
                SlashCommandOption.create(SlashCommandOptionType.STRING, "title", "Title of embed", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "Message of embed", true),
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to send embed to", false)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            String title = interaction.getArgumentByIndex(0).flatMap(SlashCommandInteractionOption::getStringRepresentationValue).orElse("No title provided");
            String message = interaction.getArgumentByIndex(1).flatMap(SlashCommandInteractionOption::getStringRepresentationValue).orElse("No message provided");

            Optional<TextChannel> channelOptional = interaction.getArgumentByIndex(2)
                    .flatMap(SlashCommandInteractionOption::getChannelValue)
                    .map(channel -> {
                        if (channel instanceof TextChannel) {
                            return (TextChannel) channel;
                        } else {
                            return null;
                        }
                    });

            if (channelOptional.isPresent()) {
                TextChannel channel = channelOptional.get();
                messageConfig.embedCommand.send(channel, new MapBuilder<String, Object>()
                        .put("title", title)
                        .put("message", message)
                        .build());
                messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                        .put("channel", channel.getIdAsString())
                        .build());
            } else {
                messageConfig.embedCommand.send(interaction.getChannel().orElseThrow(), new MapBuilder<String, Object>()
                        .put("title", title)
                        .put("message", message)
                        .build());
                messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                        .put("channel", interaction.getChannel().orElseThrow().getIdAsString())
                        .build());
            }

            responder.setFlags(MessageFlag.EPHEMERAL);
            responder.respond();
        };
    }
}
