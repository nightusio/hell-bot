package me.night.hellhard.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.hellhard.config.MessageConfig;
import me.night.hellhard.poll.Poll;
import me.night.hellhard.poll.PollManager;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private final PollManager pollManager;

    @Inject
    public PollCommand(final MessageConfig messageConfig, final PollManager pollManager) {
        super("poll", "Creates custom poll");

        this.messageConfig = messageConfig;
        this.pollManager = pollManager;

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

            Poll poll = pollManager.createPoll();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(title)
                    .setDescription(message)
                    .setFooter("HellHard | 2023 | " + poll.getId())
                    .setAuthor(event.getApi().getYourself())
                    .setColor(Color.GREEN)
                    .setTimestampToNow();

            Button yesButton = Button.primary("pollyes-"+ poll.getId(), "✔");
            Button noButton = Button.primary("pollno-"+ poll.getId(), "✖");
            Button check = Button.primary("pollcheck-"+ poll.getId(), "Sprawdz Odpowiedzi");

            ActionRow actionRow = ActionRow.of(yesButton, noButton, check);
            channel.asTextChannel().get().sendMessage(embed, actionRow);

            responder.setFlags(MessageFlag.EPHEMERAL);
            messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                    .put("channel", channel.getId())
                    .build());

            responder.respond();
        };
    }
}