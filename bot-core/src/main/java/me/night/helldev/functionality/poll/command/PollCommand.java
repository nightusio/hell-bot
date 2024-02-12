package me.night.helldev.functionality.poll.command;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.HellBot;
import me.night.helldev.config.BotConfig;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.poll.Poll;
import me.night.helldev.functionality.poll.PollManager;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.Emoji;
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
import java.util.concurrent.atomic.AtomicReference;

public class PollCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private final PollManager pollManager;
    private final HellBot hellBot;
    private final BotConfig botConfig;

    @Inject
    public PollCommand(final MessageConfig messageConfig, final PollManager pollManager, final HellBot hellBot, BotConfig botConfig) {
        super("poll", "Creates custom poll.");

        this.messageConfig = messageConfig;
        this.pollManager = pollManager;
        this.hellBot = hellBot;
        this.botConfig = botConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Arrays.asList(

                SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "Message of poll", true),
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to send poll to", false)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            String message = interaction.getArgumentByIndex(0).flatMap(SlashCommandInteractionOption::getStringValue).orElse("");
            Optional<ServerChannel> channelOption = interaction.getArgumentByIndex(1).flatMap(SlashCommandInteractionOption::getChannelValue);

            Poll poll = pollManager.createPoll();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Treść Ankiety:")
                    .setDescription("→ **" + message + "** \n")
                    .addField("", "Możliwe odpowiedzi do zaznaczenia:")
                    .addField("→ " + botConfig.upVoteId + " - Tak/Jestem za!", "**→ " + botConfig.downVoteId + " - Nie/Nie jestem za.**")
                    .setFooter("© 2024 HELLDEV.PL")
                    .setImage("https://cdn.discordapp.com/attachments/1195848786279411829/1196785430658547762/helldev-ankieta.png?ex=65b8e449&is=65a66f49&hm=78f8b62c6b8b2d3773ea38eebb9d48bf2c5f405fd5bca7825b0525e440c908ce&")
                    .setAuthor("•  HELLDEV.PL - Twój serwer code! | " + poll.getId(), "", interaction.getApi().getYourself().getAvatar())
                    .setColor(new Color(220, 3, 48))
                    .setTimestampToNow();

            CustomEmoji tak = hellBot.getDiscordApi().getCustomEmojiById("1196786062182338611").orElse(null);
            CustomEmoji nie = hellBot.getDiscordApi().getCustomEmojiById("1196786067081281627").orElse(null);
            Emoji check = hellBot.getDiscordApi().getCustomEmojiById("1206662704597700731").orElse(null);


            Button yesButton = Button.success("pollyes-"+ poll.getId(), ": 0", tak);
            Button noButton = Button.danger("pollno-"+ poll.getId(), ": 0", nie);
            Button checkButton = Button.primary("pollcheck-"+ poll.getId(), "Sprawdz kto zaglosowal", check);

            ActionRow actionRow = ActionRow.of(yesButton, noButton, checkButton);

            AtomicReference<Message> pollMessage = new AtomicReference<>();

            if (channelOption.isPresent() && channelOption.get() instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channelOption.get();
                pollMessage.set(textChannel.sendMessage(embed, actionRow).join());

                pollManager.setMessageId(poll, pollMessage.get().getId());
                pollManager.setTextChannel(poll, textChannel.getId());
            } else {
                interaction.getChannel().ifPresent(channel -> {
                    pollMessage.set(channel.sendMessage(embed, actionRow).join());

                    pollManager.setMessageId(poll, pollMessage.get().getId());
                    pollManager.setTextChannel(poll, channel.getId());
                });
            }


            responder.setFlags(MessageFlag.EPHEMERAL);
            messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                    .put("channel", pollMessage.get().getChannel().getId())
                    .build());

            responder.respond();
        };
    }

}
