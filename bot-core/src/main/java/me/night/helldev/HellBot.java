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
import lombok.Getter;
import me.night.helldev.command.TestCommand;
import me.night.helldev.command.admin.*;
import me.night.helldev.command.admin.ticket.TicketCategoryCreateCommand;
import me.night.helldev.command.admin.ticket.TicketCategoryDeleteCommand;
import me.night.helldev.command.admin.ticket.TicketCategoryListCommand;
import me.night.helldev.command.support.CheckWarnsCommand;
import me.night.helldev.command.support.WarnCommand;
import me.night.helldev.command.user.DropCommand;
import me.night.helldev.command.user.ticket.TicketAddUserCommand;
import me.night.helldev.command.user.ticket.TicketRemoveUserCommand;
import me.night.helldev.config.*;
import me.night.helldev.functionality.crash.CrashManager;
import me.night.helldev.functionality.poll.PollConfig;
import me.night.helldev.functionality.poll.command.PollCommand;
import me.night.helldev.functionality.proposition.PropositionConfig;
import me.night.helldev.functionality.proposition.PropositionManager;
import me.night.helldev.functionality.proposition.PropositionSerdes;
import me.night.helldev.functionality.ticket.TicketConfig;
import me.night.helldev.functionality.ticket.TicketHandler;
import me.night.helldev.functionality.ticket.TicketManager;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import me.night.helldev.functionality.ticket.category.TicketCategorySerdes;
import me.night.helldev.functionality.ticket.listener.TicketButtonListener;
import me.night.helldev.functionality.ticket.listener.TicketMenuListener;
import me.night.helldev.listener.ButtonListener;
import me.night.helldev.listener.JoinListener;
import me.night.helldev.functionality.proposition.listener.PropositionButtonListener;
import me.night.helldev.functionality.proposition.listener.PropositionMessageListener;
import me.night.helldev.functionality.poll.listener.PollButtonListener;
import me.night.helldev.member.MemberRepository;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.persistence.document.DocumentPersistence;
import lombok.NonNull;
import me.night.helldev.functionality.poll.PollManager;
import me.night.helldev.functionality.poll.PollSerdes;
import me.night.helldev.functionality.ticket.TicketSerdes;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.util.concurrent.atomic.AtomicReference;

public class HellBot extends DreamJavacordPlatform implements DreamPersistence, DreamJavacordConfig {

    @Getter
    private static HellBot hellBot;

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
                    }
                })
                .join();

        discordApi.updateActivity(ActivityType.COMPETING, "HellDev.pl");

        return discordApi;
    }

    @Override
    public void enable(@NonNull ComponentManager componentManager) {
        hellBot = this;

        componentManager.registerComponent(MessageConfig.class);
        componentManager.registerComponent(BotConfig.class, botConfig -> {
            componentManager.setDebug(botConfig.debug);

            this.registerInjectable(botConfig.storageConfig);

            componentManager.registerResolver(DocumentPersistenceComponentResolver.class);
            componentManager.registerResolver(DocumentRepositoryComponentResolver.class);

            componentManager.registerComponent(DocumentPersistence.class);
            componentManager.registerComponent(MemberRepository.class);
        });

        componentManager.registerComponent(ButtonListener.class);
        componentManager.registerComponent(JoinListener.class);

        componentManager.registerComponent(TicketConfig.class);
        componentManager.registerComponent(TicketCategoryManager.class);
        componentManager.registerComponent(TicketManager.class);
        componentManager.registerComponent(TicketCategoryCreateCommand.class);
        componentManager.registerComponent(TicketCategoryDeleteCommand.class);
        componentManager.registerComponent(TicketCategoryListCommand.class);
        componentManager.registerComponent(TicketHandler.class);

        componentManager.registerComponent(TicketAddUserCommand.class);
        componentManager.registerComponent(TicketRemoveUserCommand.class);

        componentManager.registerComponent(TicketButtonListener.class);
        componentManager.registerComponent(TicketMenuListener.class);

        componentManager.registerComponent(PollConfig.class);
        componentManager.registerComponent(PollManager.class);
        componentManager.registerComponent(PollCommand.class);
        componentManager.registerComponent(PollButtonListener.class);

        componentManager.registerComponent(WarnCommand.class);
        componentManager.registerComponent(SetWarnsCommand.class);
        componentManager.registerComponent(CheckWarnsCommand.class);

        componentManager.registerComponent(BanCommand.class);

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

        //todo: usun
        componentManager.registerComponent(TestCommand.class);
    }

    @Override
    public void disable() {
        hellBot = null;
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
            registry.register(new TicketCategorySerdes());
        };    }

    @Override
    public @NonNull OkaeriSerdesPack getPersistenceSerdesPack() {
        return registry -> registry.register(new SerdesJavacord());
    }
}
