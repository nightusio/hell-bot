package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;

import me.night.helldev.config.MessageConfig;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonBuilder;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.*;
import java.util.List;

public class VerifyCommand extends JavacordCommand {

    private final MessageConfig messageConfig;

    @Inject
    public VerifyCommand(final MessageConfig messageConfig) {
        super("verify", "Sends verify embed");

        this.messageConfig = messageConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        List<SlashCommandOption> optionList = new ArrayList<>(Collections.singletonList(

                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "Channel to send embed to", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();

            Channel channel = interaction.getArgumentByIndex(0).get().getChannelValue().get();

            Optional<TextChannel> textChannel = channel.asTextChannel();

            if (textChannel.isEmpty()) {
                return;
            }

            Icon icon = interaction.getApi().getYourself().getAvatar();

            EmbedBuilder embed = new EmbedBuilder()
                    .setDescription("``` " +
                            "      ✅ × HellDev.pl - Zweryfikuj się! ```\n" +
                            "> **Witaj, zweryfikuj się, aby otrzymać dostęp do wszystkich kanałów. Weryfikując się, akceptujesz regulamin.**\n" +
                            "\n" +
                            "> Nie zapomnij przywitać się z użytkownikami.")
                    .setImage("https://cdn.discordapp.com/attachments/1166458384426483764/1171815155139608666/helldev-weryfikacja.png?ex=655e0ce8&is=654b97e8&hm=f33ac4b298cb17faa0023b550d35a8cc5a3f87f17b19c40ac6aafc46a168785b&")
                    .setAuthor("HellDev - Weryfikacja", "", icon)
                    .setFooter("© 2023 helldev.pl", icon)
                    .setColor(Color.RED)
                    .setTimestampToNow();

            Button button = new ButtonBuilder()
                    .setStyle(ButtonStyle.SECONDARY)
                    .setCustomId("verify")
                    .setLabel("Zweryfikuj się")
                    .setEmoji("\uD83D\uDC4B")
                    .build();

            ActionRow actionRow = ActionRow.of(button);
            textChannel.get().sendMessage(embed, actionRow);

            responder.setFlags(MessageFlag.EPHEMERAL);
            messageConfig.embedCommandSent.applyToResponder(responder, new MapBuilder<String, Object>()
                    .put("channel", channel.getId())
                    .build());

            responder.respond();
        };
    }
}