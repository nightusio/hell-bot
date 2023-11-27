package me.night.helldev.utility;

import me.night.helldev.functionality.poll.Poll;
import me.night.helldev.functionality.proposition.Proposition;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageUpdater;
import org.javacord.api.entity.message.component.Button;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class ButtonEditUtility {

    public void editActionRowsPoll(DiscordApi api, Poll poll) {
        long textChannelId = poll.getTextChannel();
        long messageId = poll.getMessageId();

        Optional<TextChannel> textChannelOptional = api.getTextChannelById(textChannelId);

        if (textChannelOptional.isPresent()) {
            TextChannel textChannel = textChannelOptional.get();

            Message message = MessageUtility.getMessageInTextChannel(messageId, textChannel);

            if (message != null) {

                CustomEmoji tak = api.getCustomEmojiById("1169382281929031720").orElse(null);
                CustomEmoji nie = api.getCustomEmojiById("1169382274165379103").orElse(null);

                Button yesButton = Button.success("pollyes-" + poll.getId(), ": " + poll.getVotesYes(), tak);
                Button noButton = Button.danger("pollno-" + poll.getId(), ": " + poll.getVotesNo(), nie);

                MessageUpdater messageUpdater = new MessageUpdater(message)
                        .removeAllComponents()
                        .addActionRow(yesButton, noButton);

                messageUpdater.applyChanges();
            }
        }
    }

    public void editActionRowsProposition(DiscordApi api, Proposition proposition) {
        long textChannelId = proposition.getTextChannel();
        long messageId = proposition.getMessageId();

        Optional<TextChannel> textChannelOptional = api.getTextChannelById(textChannelId);

        if (textChannelOptional.isPresent()) {
            TextChannel textChannel = textChannelOptional.get();

            Message message = MessageUtility.getMessageInTextChannel(messageId, textChannel);

            if (message != null) {

                CustomEmoji tak = api.getCustomEmojiById("1169382281929031720").orElse(null);
                CustomEmoji nie = api.getCustomEmojiById("1169382274165379103").orElse(null);

                Button yesButton = Button.success("propositionyes-" + proposition.getId(), ": " + proposition.getVotesYes(), tak);
                Button noButton = Button.danger("propositionno-" + proposition.getId(), ": " + proposition.getVotesNo(), nie);

                MessageUpdater messageUpdater = new MessageUpdater(message)
                        .removeAllComponents()
                        .addActionRow(yesButton, noButton);

                messageUpdater.applyChanges();
            }
        }
    }
}
