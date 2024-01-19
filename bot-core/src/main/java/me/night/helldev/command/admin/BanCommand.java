package me.night.helldev.command.admin;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.utility.RoleUtility;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BanCommand  extends JavacordCommand {

    @Inject
    public BanCommand() {
        super("ban", "Bans user");

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.BAN_MEMBERS);

        List<SlashCommandOption> optionList = new ArrayList<>(Arrays.asList(

                SlashCommandOption.create(SlashCommandOptionType.USER, "user", "User to ban", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "description", "Ban description", true)
        ));

        this.getSlashCommandBuilder().setOptions(optionList);
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();
            Optional<Server> optionalServer = interaction.getServer();
            Optional<TextChannel> optionalChannel = interaction.getChannel();

            if (optionalServer.isEmpty()) return;
            Server server = optionalServer.get();

            if (optionalChannel.isEmpty()) return;
            TextChannel channel = optionalChannel.get();

            User bannedUser = interaction.getArgumentByIndex(0)
                    .flatMap(SlashCommandInteractionOption::getUserValue)
                    .orElse(null);

            String banDescription = interaction.getArgumentByIndex(1)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse(null);

            if (bannedUser == null) return;
            if (banDescription == null) return;

            User banningUser = interaction.getUser();

            if (!RoleUtility.hasHigherRole(banningUser, bannedUser, server)) {
                responder.setContent("Nie mozesz zbanowac tej osoby!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }

            EmbedBuilder banDmEmbed = new EmbedBuilder()
                    .setTitle("ZOSTAŁEŚ ZBANOWANY")
                    .setDescription("<:uzytkownik2:1173638447479664710> Użytkownik " + banningUser.getMentionTag() +" zbanował Cię!\n" +
                            "<:message:1196801672224186438> Powód bana: " + banDescription)
                    .setFooter("© 2024 HELLDEV.PL", interaction.getApi().getYourself().getAvatar())
                    .setAuthor("•  HELLDEV.PL - Moderacja", "", interaction.getApi().getYourself().getAvatar())
                    .setColor(Color.RED)
                    .setTimestampToNow();

            bannedUser.sendMessage(banDmEmbed);

            EmbedBuilder banChatEmbed = new EmbedBuilder()
                    .setDescription("<:uzytkownik2:1173638447479664710> Administrator: " + banningUser.getMentionTag() + "\n" +
                            "<:wave:1196801756781350994> Użytkownik: " + bannedUser.getMentionTag() + "\n"
                            + "\n" +
                            "<:message:1196801672224186438> Powód bana: " + banDescription + "\n" +
                            "<:clock:1196802606987743292> Czas bana: na zawsze"
                    )
                    .setFooter("© 2024 HELLDEV.PL", interaction.getApi().getYourself().getAvatar())
                    .setAuthor("•  HELLDEV.PL - Twój serwer code!", "", interaction.getApi().getYourself().getAvatar())
                    .setColor(Color.RED)
                    .setTimestampToNow();

            channel.sendMessage(banChatEmbed);

            server.banUser(bannedUser);

            responder.respond();
        };
    }
}