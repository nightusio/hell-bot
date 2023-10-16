package me.night.hellhard;

import cc.dreamcode.platform.DreamVersion;
import cc.dreamcode.platform.component.ComponentManager;
import cc.dreamcode.platform.javacord.DreamJavacordConfig;
import cc.dreamcode.platform.javacord.DreamJavacordPlatform;
import cc.dreamcode.platform.javacord.component.ConfigurationComponentResolver;
import cc.dreamcode.platform.javacord.exception.JavacordPlatformException;
import cc.dreamcode.platform.javacord.serdes.SerdesJavacord;
import cc.dreamcode.platform.persistence.DreamPersistence;
import cc.dreamcode.platform.persistence.component.DocumentPersistenceComponentResolver;
import cc.dreamcode.platform.persistence.component.DocumentRepositoryComponentResolver;
import me.night.hellhard.command.ReloadCommand;
import me.night.hellhard.command.admin.EmbedCommand;
import me.night.hellhard.command.admin.PollCommand;
import me.night.hellhard.command.admin.TicketCommand;
import me.night.hellhard.command.admin.VerifyCommand;
import me.night.hellhard.config.*;
import me.night.hellhard.listener.ButtonListener;
import me.night.hellhard.listener.JoinListener;
import me.night.hellhard.listener.MessageSentListener;
import me.night.hellhard.listener.poll.PollButtonListener;
import me.night.hellhard.listener.ticket.TicketButtonListener;
import me.night.hellhard.listener.ticket.TicketMenuListener;
import me.night.hellhard.member.MemberRepository;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.persistence.document.DocumentPersistence;
import lombok.NonNull;
import me.night.hellhard.poll.PollManager;
import me.night.hellhard.poll.PollSerdes;
import me.night.hellhard.ticket.TicketManager;
import me.night.hellhard.ticket.TicketSerdes;
import me.night.hellhard.ticket.impl.TicketHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.util.concurrent.atomic.AtomicReference;

public class HellBot extends DreamJavacordPlatform implements DreamPersistence, DreamJavacordConfig {

    public static void main(String[] args) {
        DreamJavacordPlatform.run(new HellBot(), args);
    }

    @Override
    public @NonNull DiscordApi login(@NonNull ComponentManager componentManager) {
        componentManager.registerResolver(ConfigurationComponentResolver.class);

        final AtomicReference<String> token = new AtomicReference<>();

        componentManager.registerComponent(TokenConfig.class, tokenConfig ->
                token.set(tokenConfig.token));

        DiscordApi discordApi = new DiscordApiBuilder()
                .setToken(token.get())
                .setAllIntents()
                .login()
                .whenComplete((api, throwable) -> {
                    if (throwable != null) {
                        throw new JavacordPlatformException("Exception while logging in to Discord", throwable);
                    } else {
                        api.updateActivity(ActivityType.PLAYING, "Your bot's status message");
                    }
                })
                .join();

        discordApi.updateActivity(ActivityType.COMPETING, "HellHard.eu");

        return discordApi;
    }

    @Override
    public void enable(@NonNull ComponentManager componentManager) {
        componentManager.registerComponent(MessageConfig.class);
        componentManager.registerComponent(BotConfig.class, botConfig -> {
            componentManager.setDebug(botConfig.debug);

            this.registerInjectable(botConfig.storageConfig);

            componentManager.registerResolver(DocumentPersistenceComponentResolver.class);
            componentManager.registerResolver(DocumentRepositoryComponentResolver.class);

            componentManager.registerComponent(DocumentPersistence.class);
            componentManager.registerComponent(MemberRepository.class);
        });

        componentManager.registerComponent(TicketConfig.class);
        componentManager.registerComponent(TicketManager.class);

        componentManager.registerComponent(PollConfig.class);
        componentManager.registerComponent(PollManager.class);
        componentManager.registerComponent(PollCommand.class);
        componentManager.registerComponent(PollButtonListener.class);

        componentManager.registerComponent(ReloadCommand.class);
        componentManager.registerComponent(EmbedCommand.class);
        componentManager.registerComponent(TicketHandler.class);
        componentManager.registerComponent(TicketCommand.class);
        componentManager.registerComponent(VerifyCommand.class);
        componentManager.registerComponent(ButtonListener.class);
        componentManager.registerComponent(TicketButtonListener.class);
        componentManager.registerComponent(TicketMenuListener.class);
        componentManager.registerComponent(MessageSentListener.class);
        componentManager.registerComponent(JoinListener.class);

    }

    @Override
    public void disable() {
        // features need to be call when server is stopping
    }

    @Override
    public @NonNull DreamVersion getDreamVersion() {
        return DreamVersion.create("HellHard", "1.0", "Nightusio");
    }

    @Override
    public @NonNull OkaeriSerdesPack getConfigSerdesPack() {
        return registry -> {
            registry.register(new TicketSerdes());
            registry.register(new PollSerdes());
        };    }

    @Override
    public @NonNull OkaeriSerdesPack getPersistenceSerdesPack() {
        return registry -> registry.register(new SerdesJavacord());
    }
}
