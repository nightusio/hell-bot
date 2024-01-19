package me.night.helldev.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.config.BotConfig;
import org.javacord.api.entity.Deletable;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JoinListener implements ServerMemberJoinListener {

    private final BotConfig botConfig;


    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        int serverMemberCount = event.getServer().getMemberCount();
        Icon icon = event.getApi().getYourself().getAvatar();

        Optional<TextChannel> optionalTextChannel = event.getApi().getTextChannelById(botConfig.welcomeChannelId);

        if(optionalTextChannel.isEmpty()) {
            System.out.println("Nie znaleziono kanalu od powitania");
            return;
        }

        TextChannel textChannel = optionalTextChannel.get();

        User joinedUser = event.getUser();

        EmbedBuilder joinEmbed = new EmbedBuilder()
                .setAuthor("•  HELLDEV.PL - Twój serwer code!", "", icon)
                .setDescription(
                        "\n" +
                        "> Witaj **" +  joinedUser.getName() + "** na helldev.pl!\n" +
                        "> Jestes " + serverMemberCount + " osobą na serwerze!\n" +
                        "\n" +
                        "> Mamy nadzieje, że zostaniesz z nami na dłużej.\n" +
                        "\n")
                .setFooter("© 2024 HELLDEV.PL ", icon)
                .setImage("https://cdn.discordapp.com/attachments/1195848786279411829/1196796800758194196/helldev-powitalnia.png?ex=65b8eee0&is=65a679e0&hm=d6303514fdef97c614f5e69105cb755902c24982ef1d04d02263f0832925d403&")
                .setThumbnail(joinedUser.getAvatar())
                .setColor(Color.RED)
                .setTimestampToNow();

        textChannel.sendMessage(joinedUser.getMentionTag());
        textChannel.sendMessage(joinEmbed);
    }
}
