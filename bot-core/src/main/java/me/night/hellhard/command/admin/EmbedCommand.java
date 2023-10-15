package me.night.hellhard.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.hellhard.config.MessageConfig;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to send embed to", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            String title = interaction.getArgumentByIndex(0).get().getStringRepresentationValue().get();
            Channel channel = interaction.getArgumentByIndex(2).get().getChannelValue().get();
            String message = interaction.getArgumentByIndex(1).get().getStringRepresentationValue().get();

            messageConfig.embedCommand.send((Messageable) channel, new MapBuilder<String, Object>()
                    .put("title", title)
                    .put("message", message)
                    .build());


            responder.setFlags(MessageFlag.EPHEMERAL);
            messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                    .put("channel", channel.getId())
                    .build());

            responder.respond();
        };
    }
}