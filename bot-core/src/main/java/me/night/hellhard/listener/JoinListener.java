package me.night.hellhard.listener;

import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.hellhard.config.BotConfig;
import me.night.hellhard.config.MessageConfig;
import org.javacord.api.entity.channel.ServerChannel;
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
        Optional<ServerChannel> welcomeChannel = event.getServer().getChannelById(botConfig.welcomeChannelId);

        if (welcomeChannel.isEmpty()) {
            return;
        }

        User joinedUser = event.getUser();

        messageConfig.welcome.send((Messageable) welcomeChannel.get(), new MapBuilder<String, Object>()
                .put("user", joinedUser.getName())
                .build());

        ((Messageable) welcomeChannel.get()).sendMessage(event.getUser().getMentionTag()).thenAcceptAsync(message -> message.delete().exceptionally(ExceptionLogger.get())).exceptionally(ExceptionLogger.get());
    }
}
