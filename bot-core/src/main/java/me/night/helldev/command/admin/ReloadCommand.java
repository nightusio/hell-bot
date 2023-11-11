package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import me.night.helldev.config.BotConfig;
import me.night.helldev.config.MessageConfig;
import cc.dreamcode.utilities.TimeUtil;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.functionality.ticket.TicketConfig;
import me.night.helldev.functionality.poll.PollConfig;
import me.night.helldev.functionality.proposition.PropositionConfig;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class ReloadCommand extends JavacordCommand {

    private final BotConfig botConfig;
    private final MessageConfig messageConfig;
    private final PollConfig pollConfig;
    private final PropositionConfig propositionConfig;
    private final TicketConfig ticketConfig;

    @Inject
    public ReloadCommand(final BotConfig botConfig, final MessageConfig messageConfig, PollConfig pollConfig, PropositionConfig propositionConfig, TicketConfig ticketConfig) {
        super("reload", "Reload configuration!");

        this.botConfig = botConfig;
        this.messageConfig = messageConfig;
        this.pollConfig = pollConfig;
        this.propositionConfig = propositionConfig;
        this.ticketConfig = ticketConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            responder.setFlags(MessageFlag.EPHEMERAL);

            final long time = System.currentTimeMillis();

            try {
                this.messageConfig.load();
                this.botConfig.load();
                this.pollConfig.load();
                this.ticketConfig.load();
                this.propositionConfig.load();

                this.messageConfig.reloadApplied.applyToResponder(responder, new MapBuilder<String, Object>()
                        .put("time", TimeUtil.convertMills(System.currentTimeMillis() - time))
                        .build());
            }
            catch (NullPointerException | OkaeriException e) {
                e.printStackTrace();

                this.messageConfig.reloadFailed.applyToResponder(responder, new MapBuilder<String, Object>()
                        .put("reason", e.getMessage())
                        .build());
            }

            responder.respond();
        };
    }
}
