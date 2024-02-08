package me.night.helldev.functionality.crash;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import me.night.helldev.HellBot;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Optional;

@UtilityClass
public class CrashManager {

    public void sendCrashMessage(String cause) {
        DiscordApi discordApi = HellBot.getHellBot().getDiscordApi();

        Optional<TextChannel> optionalTextChannel = discordApi.getTextChannelById("1204964746076618772");

        if (optionalTextChannel.isEmpty()) return;

        TextChannel textChannel = optionalTextChannel.get();

        textChannel.sendMessage("@here COS POSZLO NIE TAK " + cause);
    }
}
