package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.HellBot;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.poll.Poll;
import me.night.helldev.functionality.poll.PollManager;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
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
import java.util.Optional;

public class PollCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private final PollManager pollManager;
    private final HellBot hellBot;

    @Inject
    public PollCommand(final MessageConfig messageConfig, final PollManager pollManager, final HellBot hellBot) {
        super("poll", "Creates custom poll");

        this.messageConfig = messageConfig;
        this.pollManager = pollManager;
        this.hellBot = hellBot;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Arrays.asList(

                SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "Message of poll", true),
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to send poll to", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            Channel channel = interaction.getArgumentByIndex(1).get().getChannelValue().get();
            String message = interaction.getArgumentByIndex(0).get().getStringRepresentationValue().get();

            Poll poll = pollManager.createPoll();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Treść Ankiety:")
                    .setDescription("→ **" + message + "** \n")
                    .addField("", "Możliwe odpowiedzi do zaznaczenia:")
                    .addField("→ <:tak:1169382281929031720> - Tak/Jestem za!", "**→ <:nie:1169382274165379103> - Nie/Nie jestem za.**")
                    .setFooter("© 2023 HellDev | " + poll.getId())
                    .setImage("https://cdn.discordapp.com/attachments/1166458384426483764/1171555805233959013/helldev-ankieta.png?ex=655d1b5e&is=654aa65e&hm=88e8903fc131fd8df4c871069edfcabe261f3a6894b106e1b33a430d3ce5d59e&")
                    .setAuthor("HellDev - Ankieta", "", interaction.getApi().getYourself().getAvatar())
                    .setColor(Color.GREEN)
                    .setTimestampToNow();

            CustomEmoji tak = hellBot.getDiscordApi().getCustomEmojiById("1169382281929031720").orElse(null);
            CustomEmoji nie = hellBot.getDiscordApi().getCustomEmojiById("1169382274165379103").orElse(null);

            Button yesButton = Button.success("pollyes-"+ poll.getId(), ": 0", tak);
            Button noButton = Button.danger("pollno-"+ poll.getId(), ": 0", nie);

            ActionRow actionRow = ActionRow.of(yesButton, noButton);

            Optional<TextChannel> optionalPollChannel = channel.asTextChannel();

            if(optionalPollChannel.isEmpty()) return;

            Message pollMessage = optionalPollChannel.get().sendMessage(embed, actionRow).join();

            pollManager.setMessageId(poll, pollMessage.getId());
            pollManager.setTextChannel(poll, pollMessage.getChannel().getId());

            responder.setFlags(MessageFlag.EPHEMERAL);
            messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                    .put("channel", channel.getId())
                    .build());

            responder.respond();
        };
    }
}
