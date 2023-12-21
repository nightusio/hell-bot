package me.night.helldev.command;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.MessageConfig;

public class TestCommand extends JavacordCommand {

    private final MessageConfig messageConfig;

    @Inject
    public TestCommand(final MessageConfig messageConfig) {
        super("test", "test select");

        this.messageConfig = messageConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {

            event.getInteraction().respondWithModal("modalId","Modal Title",
                    ActionRow.of(TextInput.create(TextInputStyle.PARAGRAPH, "text_input_id", "This is a Text Input Field")));
        };
    }

}
