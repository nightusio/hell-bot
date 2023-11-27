package me.night.helldev.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.config.BotConfig;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

import java.awt.*;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JoinListener implements ServerMemberJoinListener {

    private final BotConfig botConfig;


    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        System.out.println("1");
        int serverMemberCount = event.getServer().getMemberCount();
        Icon icon = event.getApi().getYourself().getAvatar();

        Optional<TextChannel> optionalTextChannel = event.getApi().getTextChannelById(botConfig.welcomeChannelId);

        if(optionalTextChannel.isEmpty()) {
            System.out.println("chuj kutas nie dziala");
            return;
        }

        TextChannel textChannel = optionalTextChannel.get();

        User joinedUser = event.getUser();

        EmbedBuilder joinEmbed = new EmbedBuilder()
                .setAuthor("helldev.pl - Powitalnia", "https://helldev.pl", icon)
                .setDescription("```       \uD83D\uDCE7 × HellDev.pl - Powitalnia       ```\n" +
                        "\n" +
                        "> Witaj **" +  joinedUser.getName() + "** na helldev.pl!\n" +
                        "> Jestes " + serverMemberCount + " osobą na serwerze!\n" +
                        "\n" +
                        "> Mamy nadzieje, że zostaniesz z nami na dłużej.\n" +
                        "\n")
                .setFooter("© 2023 helldev.pl • ", icon)
                .setThumbnail(joinedUser.getAvatar())
                .setColor(Color.RED)
                .setTimestampToNow();

        textChannel.sendMessage(joinedUser.getMentionTag()).thenAccept(message -> message.delete("."));
        textChannel.sendMessage(joinEmbed);
    }
}
