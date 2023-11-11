package me.night.helldev;

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
import me.night.helldev.command.admin.*;
import me.night.helldev.command.user.DropCommand;
import me.night.helldev.config.*;
import me.night.helldev.functionality.poll.PollConfig;
import me.night.helldev.functionality.proposition.PropositionConfig;
import me.night.helldev.functionality.proposition.PropositionManager;
import me.night.helldev.functionality.proposition.PropositionSerdes;
import me.night.helldev.functionality.ticket.TicketConfig;
import me.night.helldev.listener.ButtonListener;
import me.night.helldev.listener.JoinListener;
import me.night.helldev.listener.proposition.PropositionButtonListener;
import me.night.helldev.listener.proposition.PropositionMessageListener;
import me.night.helldev.listener.poll.PollButtonListener;
import me.night.helldev.listener.ticket.TicketButtonListener;
import me.night.helldev.listener.ticket.TicketMenuListener;
import me.night.helldev.member.MemberRepository;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.persistence.document.DocumentPersistence;
import lombok.NonNull;
import me.night.helldev.functionality.poll.PollManager;
import me.night.helldev.functionality.poll.PollSerdes;
import me.night.helldev.functionality.ticket.TicketManager;
import me.night.helldev.functionality.ticket.TicketSerdes;
import me.night.helldev.functionality.ticket.impl.TicketHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;

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
                .addIntents(Intent.GUILD_INTEGRATIONS, Intent.DIRECT_MESSAGES, Intent.MESSAGE_CONTENT, Intent.GUILD_INVITES)
                .login()
                .whenComplete((api, throwable) -> {
                    if (throwable != null) {
                        throw new JavacordPlatformException("Exception while logging in to Discord", throwable);
                    }
                })
                .join();

        discordApi.updateActivity(ActivityType.COMPETING, "HellDev.pl");

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
        componentManager.registerComponent(TicketHandler.class);
        componentManager.registerComponent(TicketButtonListener.class);
        componentManager.registerComponent(TicketMenuListener.class);

        componentManager.registerComponent(PollConfig.class);
        componentManager.registerComponent(PollManager.class);
        componentManager.registerComponent(PollCommand.class);
        componentManager.registerComponent(PollButtonListener.class);

        componentManager.registerComponent(PropositionConfig.class);
        componentManager.registerComponent(PropositionManager.class);
        componentManager.registerComponent(PropositionMessageListener.class);
        componentManager.registerComponent(PropositionButtonListener.class);

        componentManager.registerComponent(ReloadCommand.class);
        componentManager.registerComponent(EmbedCommand.class);
        componentManager.registerComponent(TicketCommand.class);
        componentManager.registerComponent(VerifyCommand.class);
        componentManager.registerComponent(ClearCommand.class);
        componentManager.registerComponent(DropCommand.class);

        componentManager.registerComponent(ButtonListener.class);
        componentManager.registerComponent(JoinListener.class);

    }

    @Override
    public void disable() {

    }

    @Override
    public @NonNull DreamVersion getDreamVersion() {
        return DreamVersion.create("HellDev", "1.0", "Nightusio");
    }

    @Override
    public @NonNull OkaeriSerdesPack getConfigSerdesPack() {
        return registry -> {
            registry.register(new TicketSerdes());
            registry.register(new PollSerdes());
            registry.register(new PropositionSerdes());
        };    }

    @Override
    public @NonNull OkaeriSerdesPack getPersistenceSerdesPack() {
        return registry -> registry.register(new SerdesJavacord());
    }
}
