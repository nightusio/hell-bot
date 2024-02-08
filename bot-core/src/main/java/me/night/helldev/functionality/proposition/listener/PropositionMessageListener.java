package me.night.helldev.functionality.proposition.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.config.BotConfig;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.proposition.Proposition;
import me.night.helldev.functionality.proposition.PropositionConfig;
import me.night.helldev.functionality.proposition.PropositionManager;
import me.night.helldev.utility.MessageUtility;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PropositionMessageListener implements MessageCreateListener {

    private final MessageConfig messageConfig;
    private final BotConfig botConfig;
    private final PropositionManager propositionManager;
    private final PropositionConfig propositionConfig;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        TextChannel textChannel = event.getChannel();
        Optional<User> optionalUser = event.getMessage().getUserAuthor();

        if (textChannel.getId() != Long.parseLong(botConfig.propositionChannelId) || !optionalUser.isPresent() || optionalUser.get().isBot())
            return;

        User user = optionalUser.get();

        if (botConfig.blockedWords.stream().anyMatch(blockedWord -> event.getMessage().getContent().contains(blockedWord))) {
            user.openPrivateChannel().thenAccept(privateChannel -> messageConfig.propositionNotSend.send(privateChannel));
            return;
        }

        Proposition proposition = propositionManager.createProposition();
        propositionConfig.save();

        createPropositionMessage(event, user, proposition);
    }

    private void createPropositionMessage(MessageCreateEvent event, User user, Proposition proposition) {
        TextChannel textChannel = event.getChannel();

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("• HELLDEV.PL - Twój serwer code!", String.valueOf(event.getApi().getYourself().getAvatar().getUrl()), event.getApi().getYourself().getAvatar())
                .setThumbnail(user.getAvatar())
                .setTitle("Propozycja użytkownika: " + user.getName())
                .setDescription("→ **" + event.getMessage().getContent() + "** \n")
                .addField("", "")
                .addField("", "Zaznacz poniżej:")
                .addField("", "")
                .addField("→ " + botConfig.upVoteId + " - Tak/Jestem za!", "**→ " + botConfig.downVoteId + " - Nie/Nie jestem za.**")
                .setFooter("© 2024 HELLDEV.PL", event.getApi().getYourself().getAvatar())
                .setImage("https://cdn.discordapp.com/attachments/1195848786279411829/1196791078381162496/helldev-propozycje.png?ex=65b8e98c&is=65a6748c&hm=35ac2206c3c1edc64fbe03e671e24ddcb53d31d9ba35fb1991aa919ed5816d52&")
                .setColor(Color.RED)
                .setTimestampToNow();

        CustomEmoji tak = event.getApi().getCustomEmojiById("1196786062182338611").orElse(null);
        CustomEmoji nie = event.getApi().getCustomEmojiById("1196786067081281627").orElse(null);

        Button yesButton = Button.success("propositionyes-" + proposition.getId(), ": 0", tak);
        Button noButton = Button.danger("propositionno-" + proposition.getId(), ": 0", nie);

        ActionRow actionRow = ActionRow.of(yesButton, noButton);

        Message originMessage = event.getMessage();

        textChannel.sendMessage(embed, actionRow)
                .thenAccept(message -> {
                    originMessage.deleteAfter(Duration.ofSeconds(1));
                    propositionManager.setMessageId(proposition, message.getId());
                    propositionManager.setTextChannel(proposition, message.getChannel().getId());
                    MessageUtility.openDiscussion(message, user.getName());
                });
    }
}
