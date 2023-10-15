package me.night.stormcity;

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
import me.night.stormcity.command.ReloadCommand;
import me.night.stormcity.command.admin.EmbedCommand;
import me.night.stormcity.command.admin.TicketCommand;
import me.night.stormcity.command.admin.VerifyCommand;
import me.night.stormcity.config.BotConfig;
import me.night.stormcity.config.MessageConfig;
import me.night.stormcity.config.TicketConfig;
import me.night.stormcity.config.TokenConfig;
import me.night.stormcity.listener.ButtonListener;
import me.night.stormcity.listener.JoinListener;
import me.night.stormcity.listener.MessageSentListener;
import me.night.stormcity.listener.ticket.TicketButtonListener;
import me.night.stormcity.listener.ticket.TicketMenuListener;
import me.night.stormcity.member.MemberRepository;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.persistence.document.DocumentPersistence;
import lombok.NonNull;
import me.night.stormcity.ticket.TicketManager;
import me.night.stormcity.ticket.TicketSerdes;
import me.night.stormcity.ticket.impl.TicketHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.concurrent.atomic.AtomicReference;

public class StormBot extends DreamJavacordPlatform implements DreamPersistence, DreamJavacordConfig {

    public static void main(String[] args) {
        DreamJavacordPlatform.run(new StormBot(), args);
    }

    @Override
    public @NonNull DiscordApi login(@NonNull ComponentManager componentManager) {
        componentManager.registerResolver(ConfigurationComponentResolver.class);

        final AtomicReference<String> token = new AtomicReference<>();

        componentManager.registerComponent(TokenConfig.class, tokenConfig ->
                token.set(tokenConfig.token));

        return new DiscordApiBuilder()
                .setToken(token.get())
                .setAllIntents()
                .login()
                .whenComplete((discordApi, throwable) -> {
                    if (throwable != null) {
                        throw new JavacordPlatformException("Exception while logging in to Discord");
                    }
                })
                .join();
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
        return DreamVersion.create("StormCity", "1.0", "Nightusio");
    }

    @Override
    public @NonNull OkaeriSerdesPack getConfigSerdesPack() {
        return registry -> registry.register(new TicketSerdes());
    }

    @Override
    public @NonNull OkaeriSerdesPack getPersistenceSerdesPack() {
        return registry -> registry.register(new SerdesJavacord());
    }
}
