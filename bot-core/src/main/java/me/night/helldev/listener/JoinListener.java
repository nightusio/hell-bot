package me.night.helldev.listener;

import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.config.BotConfig;
import me.night.helldev.config.MessageConfig;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JoinListener implements ServerMemberJoinListener {

    private final BotConfig botConfig;
    private final MessageConfig messageConfig;


    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        Optional<TextChannel> optionalTextChannel = event.getApi().getTextChannelById(botConfig.welcomeChannelId);

        if(optionalTextChannel.isEmpty()) return;

        TextChannel textChannel = optionalTextChannel.get();

        User joinedUser = event.getUser();

        messageConfig.welcome.send(textChannel, new MapBuilder<String, Object>()
                .put("user", joinedUser.getName())
                .build());

        textChannel.sendMessage(event.getUser().getMentionTag()).thenAcceptAsync(message -> message.delete().exceptionally(ExceptionLogger.get())).exceptionally(ExceptionLogger.get());
    }
}
