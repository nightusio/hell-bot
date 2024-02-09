package me.night.helldev.command.admin.ticket;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import com.vdurmont.emoji.EmojiParser;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.MessageConfig;
import me.night.helldev.functionality.ticket.category.TicketCategory;
import me.night.helldev.functionality.ticket.category.TicketCategoryManager;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.*;

public class TicketCategoryCreateCommand extends JavacordCommand {

    private final MessageConfig messageConfig;
    private final TicketCategoryManager ticketCategoryManager;

    @Inject
    public TicketCategoryCreateCommand(final MessageConfig messageConfig, final TicketCategoryManager ticketCategoryManager) {
        super("ticketcategorycreate", "Creates new ticket category");

        this.messageConfig = messageConfig;
        this.ticketCategoryManager = ticketCategoryManager;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = Arrays.asList(
                SlashCommandOption.create(SlashCommandOptionType.STRING, "id", "Id name of ticket category ex. `usluga` ", true),
                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "category", "Category that will store tickets of given type", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "Name that will be displayed in menu", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "description", "Description that will be displayed in menu", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "emoji", "Emoji that will be displayed", true)
        );

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            interaction.getOptionByName("id").ifPresentOrElse(idOption -> {
                String id = idOption.getStringValue().orElseThrow();
                interaction.getOptionByName("category").flatMap(SlashCommandInteractionOption::getChannelValue).ifPresentOrElse(serverChannel -> {
                    String name = interaction.getOptionByName("name").flatMap(SlashCommandInteractionOption::getStringValue).orElseThrow();
                    String description = interaction.getOptionByName("description").flatMap(SlashCommandInteractionOption::getStringValue).orElseThrow();
                    String emoji = interaction.getOptionByName("emoji").flatMap(SlashCommandInteractionOption::getStringValue).orElseThrow();

                    long categoryId = serverChannel.getId();

                    try {
                        String unicode = emojiToUnicode(emoji);

                        TicketCategory ticketCategory = new TicketCategory(id, categoryId, name, description, unicode);
                        ticketCategoryManager.createTicketCategory(ticketCategory);

                        messageConfig.successfullyCreateTicketCategory.applyToResponder(responder, Map.of(
                                "category_name", id,
                                "category_id", categoryId
                        ));
                    } catch (OkaeriException exception) {
                        messageConfig.cannotCreateTicketCategory.applyToResponder(responder, Map.of(
                                "error", "Failed to create ticket category: " + exception.getMessage()
                        ));
                    }
                }, () -> {
                    responder.setFlags(MessageFlag.EPHEMERAL);
                    messageConfig.cannotCreateTicketCategory.applyToResponder(responder, Map.of(
                            "error", "Channel option not present"
                    ));
                });
            }, () -> {
                responder.setFlags(MessageFlag.EPHEMERAL);
                messageConfig.cannotCreateTicketCategory.applyToResponder(responder, Map.of(
                        "error", "ID option not present"
                ));
            });

            responder.respond();
        };
    }

    private static String emojiToUnicode(String emoji) {
        return EmojiParser.parseToUnicode(emoji);
    }
}
