package me.night.helldev.command.admin.ticket;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import com.vdurmont.emoji.EmojiParser;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketCategoryCreateCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private final TicketCategoryManager ticketCategoryManager;

    @Inject
    public TicketCategoryCreateCommand(final MessageConfig messageConfig, final TicketCategoryManager ticketCategoryManager) {
        super("ticketcategorycreate", "Creates new ticket category");

        this.messageConfig = messageConfig;
        this.ticketCategoryManager = ticketCategoryManager;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Arrays.asList(

                SlashCommandOption.create(SlashCommandOptionType.STRING, "id", "Id name of ticket category ex. `usluga` ", true),
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "category", "Category that will store tickets of given type", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "Name that will be displayed in menu", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "description", "Description that will be displayed in menu", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "emoji", "Emoji that will be displayed", true)

        ));
        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            String id = interaction.getArgumentByIndex(0)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse(null);

            ServerChannel serverChannel = interaction.getArgumentByIndex(1)
                    .flatMap(SlashCommandInteractionOption::getChannelValue)
                    .orElse(null);

            String name = interaction.getArgumentByIndex(2)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse(null);

            String description = interaction.getArgumentByIndex(3)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse(null);

            String emoji = interaction.getArgumentByIndex(4)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse(null);

            if (id == null) return;
            if (serverChannel == null) return;
            if (emoji == null) return;

            long categoryId = serverChannel.getId();

            try {
                String unicode = emojiToUnicode(emoji);

                TicketCategory ticketCategory = new TicketCategory(id, categoryId, name, description, unicode );
                ticketCategoryManager.createTicketCategory(ticketCategory);

                messageConfig.successfullyCreateTicketCategory.applyToResponder(responder, new MapBuilder<String, Object>()
                        .put("category_name", id)
                        .put("category_id", categoryId)
                        .build());

            } catch (OkaeriException | NullPointerException exception) {
                messageConfig.cannotCreateTicketCategory.applyToResponder(responder, new MapBuilder<String, Object>()
                        .put("error", exception.getCause().toString())
                        .build());
            }

            responder.respond();
        };
    }

    public static String emojiToUnicode(String emoji) {
        return EmojiParser.parseToUnicode(emoji);
    }
}