package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.BotConfig;
import me.night.helldev.config.MessageConfig;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.Collections;

public class SetWarnsCommand extends JavacordCommand {

    private final BotConfig botConfig;
    private final MessageConfig messageConfig;

    @Inject
    public SetWarnsCommand(BotConfig botConfig, MessageConfig messageConfig) {
        super("setmaxwarns", "Sets max warns (after max warns, the user is banned)");
        this.botConfig = botConfig;
        this.messageConfig = messageConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
        this.getSlashCommandBuilder().setOptions(Collections.singletonList(
                SlashCommandOption.create(SlashCommandOptionType.STRING, "number", "Amount of max warns", true)
        ));
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL);

            String amount = interaction.getArgumentByIndex(0)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse(null);

            if (amount == null) {
                return;
            }

            try {
                int warnsToBan = Integer.parseInt(amount);

                botConfig.maxWarns = warnsToBan;
                botConfig.save();
                responder.setContent("Max warns has been set to " + warnsToBan + "!").respond();
            } catch (NumberFormatException e) {
                messageConfig.notNumber.applyToResponder(responder);
            } catch (OkaeriException e) {

            }
        };
    }
}
