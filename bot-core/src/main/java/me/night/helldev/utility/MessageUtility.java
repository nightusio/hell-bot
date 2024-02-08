package me.night.helldev.utility;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.channel.AutoArchiveDuration;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

@UtilityClass
public class MessageUtility {

    public void openDiscussion(Message message, String userName) {
        message.createThread("Dyskusja na temat propozycji użytkownika: " + userName, AutoArchiveDuration.ONE_WEEK);
    }

    public Message getMessageInTextChannel(long messageId, TextChannel textChannel) {
        return textChannel.getMessageById(messageId).join();
    }

    public void respondWithEphemeralMessage(SelectMenuChooseEvent event, String content) {
        event.getInteraction().createImmediateResponder()
                .setContent(content)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    public void respondWithEphemeralMessage(SlashCommandCreateEvent event, String content) {
        event.getInteraction().createImmediateResponder()
                .setContent(content)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    public void respondWithEphemeralMessage(ButtonClickEvent event, String content) {
        event.getInteraction().createImmediateResponder()
                .setContent(content)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }
}
