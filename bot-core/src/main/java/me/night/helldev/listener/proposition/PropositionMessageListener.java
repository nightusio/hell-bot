package me.night.helldev.listener.proposition;

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
                .setAuthor("HellDev.pl - Propozycja", String.valueOf(event.getApi().getYourself().getAvatar().getUrl()), event.getApi().getYourself().getAvatar())
                .setThumbnail(user.getAvatar())
                .setTitle("Propozycja uzytkownika: **" + user.getName() + "**")
                .setDescription("→ **" + originalMessage.getContent() + "** \n")
                .addField("", "")
                .addField("", "Zaznacz poniżej:")
                .addField("", "")
                .addField("→ <:tak:1169382281929031720> - Tak/Jestem za!", "**→ <:nie:1169382274165379103> - Nie/Nie jestem za.**")
                .setFooter("© 2023 HellDev.pl | " + proposition.getId(), event.getApi().getYourself().getAvatar())
                .setImage("https://cdn.discordapp.com/attachments/1166458384426483764/1171555807368855662/helldev-propozycje.png?ex=655d1b5e&is=654aa65e&hm=96b90e71d462c919791642514bdf2c5d00e4c240a16e2424fb1e6400e305d298&")
                .setColor(Color.RED)
                .setTimestampToNow();

        CustomEmoji tak = event.getApi().getCustomEmojiById("1169382281929031720").orElse(null);
        CustomEmoji nie = event.getApi().getCustomEmojiById("1169382274165379103").orElse(null);

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
