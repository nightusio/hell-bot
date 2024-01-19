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
        Message originalMessage = event.getMessage();

        long textChannelId = textChannel.getId();

        if (!(textChannelId == Long.parseLong(botConfig.propositionChannelId))) {
            return;
        }

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        if (user.isBot()) {
            return;
        }

        originalMessage.delete();

        if (botConfig.blockedWords.stream().anyMatch(blockedWord -> originalMessage.getContent().contains(blockedWord))) {
            user.openPrivateChannel().thenAccept(privateChannel -> messageConfig.propositionNotSend.send(privateChannel));
            return;
        }

        Proposition proposition = propositionManager.createProposition();

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("•  HELLDEV.PL - Twój serwer code!", String.valueOf(event.getApi().getYourself().getAvatar().getUrl()), event.getApi().getYourself().getAvatar())
                .setThumbnail(user.getAvatar())
                .setTitle("Propozycja uzytkownika: **" + user.getName() + "**")
                .setDescription("→ **" + originalMessage.getContent() + "** \n")
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

        org.javacord.api.entity.message.component.Button yesButton = org.javacord.api.entity.message.component.Button.success("propositionyes-"+ proposition.getId(), ": 0", tak);
        org.javacord.api.entity.message.component.Button noButton = Button.danger("propositionno-"+ proposition.getId(), ": 0", nie);

        ActionRow actionRow = ActionRow.of(yesButton, noButton);

        Message propositionMessage = textChannel.asTextChannel().get().sendMessage(embed, actionRow).join();
        propositionManager.setMessageId(proposition, propositionMessage.getId());
        propositionManager.setTextChannel(proposition, propositionMessage.getChannel().getId());
        propositionConfig.save();
        MessageUtility.openDiscussion(propositionMessage, user.getName());

    }
}
