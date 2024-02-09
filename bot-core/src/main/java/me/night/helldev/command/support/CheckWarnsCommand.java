package me.night.helldev.command.support;

import cc.dreamcode.platform.javacord.component.command.JavacordCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.night.helldev.member.Member;
import me.night.helldev.member.MemberRepository;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.Collections;

public class CheckWarnsCommand extends JavacordCommand {

    private final MemberRepository memberRepository;

    @Inject
    public CheckWarnsCommand(MemberRepository memberRepository) {
        super("checkwarns", "Check the number of warns for a user");
        this.memberRepository = memberRepository;

        this.getSlashCommandBuilder().setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR);

        this.getSlashCommandBuilder().setOptions(Collections.singletonList(
                SlashCommandOption.create(SlashCommandOptionType.USER, "user", "User to check warns for", true)
        ));
    }

    @Override
    public @NonNull SlashCommandCreateListener respond() {
        return event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL);

            interaction.getArgumentByIndex(0)
                    .flatMap(SlashCommandInteractionOption::getUserValue)
                    .ifPresent(user -> {

                        Member member = memberRepository.findOrCreate(user);

                        int warns = member.getWarns().size();

                        if (warns == 0) {
                            responder.setContent(user.getMentionTag() + " nie ma ostrzezen.").setFlags(MessageFlag.EPHEMERAL).respond();

                        } else {
                            responder.setContent(user.getMentionTag() + " ma " + warns + " ostrzezen.").setFlags(MessageFlag.EPHEMERAL).respond();

                        }
                    });
        };
    }
}
