package me.night.helldev.command.support;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.config.BotConfig;
import me.night.helldev.member.Member;
import me.night.helldev.member.MemberRepository;
import me.night.helldev.utility.RoleUtility;
import org.javacord.api.entity.Icon;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WarnCommand  extends JavacordCommand {

    private final MemberRepository memberRepository;
    private final BotConfig botConfig;

    @Inject
    public WarnCommand(MemberRepository memberRepository, BotConfig botConfig) {
        super("warn", "Warns a user");
        this.memberRepository = memberRepository;
        this.botConfig = botConfig;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.KICK_MEMBERS);

        List<SlashCommandOption> optionList = new ArrayList<>(Arrays.asList(

                SlashCommandOption.create(SlashCommandOptionType.USER, "user", "User to warn", true),
                SlashCommandOption.create(SlashCommandOptionType.STRING, "description", "Warn description", false)
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

            User warnedUser = interaction.getArgumentByIndex(0)
                    .flatMap(SlashCommandInteractionOption::getUserValue)
                    .orElse(null);

            String warnDescription = interaction.getArgumentByIndex(1)
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse("Brak");

            if (warnedUser == null) return;

            User warningUser = interaction.getUser();

            if (!RoleUtility.hasHigherRole(warningUser, warnedUser, server)) {
                responder.setContent("Nie mozesz ostrzec tej osoby!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }

            Member member = memberRepository.findOrCreate(warnedUser);

            member.getWarns().add(warnDescription);
            member.save();

            Icon icon = interaction.getApi().getYourself().getAvatar();

            EmbedBuilder warnEmbed = new EmbedBuilder()
                    .setDescription("``` " +
                            "     ⚠️ × HellDev.pl - Warn! ```\n" +
                            "> **Uzytkownik " + warnedUser.getMentionTag() + " zostal ostrzezony!.**\n" +
                            "> **Powod: " + warnDescription +  " .**\n" +
                            "> **Jest to " + member.getWarns().size() + " ostrzezenie.**" +
                            "\n")
                    .setAuthor("HellDev - Warn", "", icon)
                    .setFooter("© 2023 helldev.pl", icon)
                    .setColor(Color.RED)
                    .setTimestampToNow();


            if (member.getWarns().size() >= botConfig.maxWarns) {
                server.banUser(warnedUser);
            }

            channel.sendMessage(warnEmbed);

            responder.setFlags(MessageFlag.EPHEMERAL);
            interaction.createImmediateResponder().setContent("Uzytkownik " + warnedUser.getMentionTag() + " zostal pomyslnie ostrzezony!").respond();


            responder.respond();
        };
    }
}