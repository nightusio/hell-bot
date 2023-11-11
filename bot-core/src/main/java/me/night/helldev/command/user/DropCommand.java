package me.night.helldev.command.user;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.BotConfig;
import me.night.helldev.config.MessageConfig;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.Optional;
import java.util.Random;

public class DropCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private final BotConfig botConfig;
    private final Random random = new Random();

    @Inject
    public DropCommand(final MessageConfig messageConfig, final BotConfig botConfig) {
        super("drop", "Szansa na wydropienie rangi premium");

        this.messageConfig = messageConfig;
        this.botConfig = botConfig;
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            TextChannel textChannel = interaction.getApi().getTextChannelById("1171472908254728323").get();
            User user = interaction.getUser();

            double premiumDropChance = 0.0001; // 0.01%
            double discount10DropChance = 0.01; // 1%

            double randomValue = random.nextDouble();

            if (!interaction.getChannel().get().getIdAsString().equals("1171903180976693288")) {

                interaction.createImmediateResponder().setContent("Nie ten kanal.").setFlags(MessageFlag.EPHEMERAL).respond();
                responder.respond();
                return;
            }

            if (randomValue < premiumDropChance) {
                responder.setFlags(MessageFlag.EPHEMERAL);

                interaction.createImmediateResponder().setContent("Wygrales **dostep do strefy premium! (0.01%)**!").setFlags(MessageFlag.EPHEMERAL).respond();
                textChannel.sendMessage("Uzytkownik: " + interaction.getUser().getName() + " wydropil range premium");

                botConfig.premiumRoleIds.stream()
                        .map(roleId -> event.getApi().getRoleById(roleId))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(user::addRole);
                return;

            } else if (randomValue < discount10DropChance) {
                interaction.createImmediateResponder().setContent("Wygrales **zniżke 10% (1%)**!").setFlags(MessageFlag.EPHEMERAL).respond();
                textChannel.sendMessage("Uzytkownik: " + interaction.getUser().getName() + " wydropil znizke 10%");

                botConfig.discount10Ids.stream()
                        .map(roleId -> event.getApi().getRoleById(roleId))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(user::addRole);
                return;
            }

            interaction.createImmediateResponder().setContent("Nic nie wygrales :(!").setFlags(MessageFlag.EPHEMERAL).respond();

            responder.respond();
        };
    }
}