package me.night.hellhard.listener;

import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.hellhard.config.BotConfig;
import me.night.hellhard.config.MessageConfig;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageSentListener implements MessageCreateListener {

    private final MessageConfig messageConfig;
    private final BotConfig botConfig;

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


        messageConfig.proposition.send(textChannel, new MapBuilder<String, Object>()
                .put("user", user.getName())
                .put("message", originalMessage.getContent())
                .build()
        ).thenAccept(messageToSend -> {
            messageToSend.addReaction("\uD83D\uDC4D");
            messageToSend.addReaction("\uD83D\uDC4E");
            messageToSend.addReaction("❤");
        });

    }
}
