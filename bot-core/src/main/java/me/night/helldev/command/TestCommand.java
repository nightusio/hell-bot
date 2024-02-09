package me.night.helldev.command;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.night.helldev.config.MessageConfig;
import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

@Slf4j
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
                ImageCaptcha imageCaptcha = ImageCaptcha.create();

                SlashCommandInteraction interaction = event.getSlashCommandInteraction();
                InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

                event.getInteraction().respondWithModal("verifymodal","Weryfikacja",
                        ActionRow.of(TextInput.create(TextInputStyle.SHORT, "verifytext", imageCaptcha.getContent())));

                responder.respond();
        };
    }

}
