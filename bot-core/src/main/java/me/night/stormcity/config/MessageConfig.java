package me.night.stormcity.config;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import cc.dreamcode.platform.javacord.embed.WrappedEmbedBuilder;
import cc.dreamcode.platform.javacord.notice.Notice;
import cc.dreamcode.platform.javacord.notice.NoticeType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Headers;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

@Configuration(child = "messages.yml")
@Headers({
        @Header("## StormCity (Message-Config) ##"),
        @Header("Dostepne type: (MESSAGE, EMBED)")
})
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MessageConfig extends OkaeriConfig {

    public Notice reloadFailed = new Notice(NoticeType.MESSAGE, "``[ ❌ ] Wystapil problem z przeladowanie konfiguracji! ({reason})``");
    public Notice reloadApplied = new Notice(NoticeType.MESSAGE, "``[ ✅ ] Przeladowano konfiguracje! ({time})``");

    @Comment("Example bot embed")
    public Notice embedBuilder = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("Example title")
            .setDescription("Example description")
            .setAuthor("{user-nick}", "http://google.com/", "https://cdn.discordapp.com/avatars/{user-id}/{user-avatar}.png")
            .addField("A field", "Some text inside the field")
            .addInlineField("An inline field", "More text")
            .addInlineField("Another inline field", "Even more text")
            .setColor(Color.BLUE)
            .setFooter("{user-nick}", "https://cdn.discordapp.com/avatars/{user-id}/{user-avatar}.png"));

    @Comment("/embed")
    public Notice embedCommand = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("{title}")
            .setDescription("{message}")
            .setFooter("StormCity | 2023")
            .setTimestampToNow());

    public Notice embedCommandSent = new Notice(NoticeType.MESSAGE, "[ ✅ ] Wyslano wiadomosc na kanal: <#{channel}>");

    public String ticketCreated = "[ ✅ ] Pomyslnie stworzyles ticket! <#{channel}>!";

    public String verifiedSuccess = "[ ✅ ] Pomyslnie sie zweryfikowales!";

    @Comment("Propozycja")
    public Notice proposition = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("Propozycja uzytkownika: {user}")
            .setDescription("{message}")
            .setFooter("StormCity | 2023")
            .setTimestampToNow());

    @Comment("Powitanie")
    public Notice welcome = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("Witaj {user}")
            .setDescription("Pogalopuj na regulamin I przeskocz przez role! Życzymy miłego pobytu!")
            .setFooter("StormCity | 2023")
            .setTimestampToNow());


    public Notice propositionNotSend = new Notice(NoticeType.MESSAGE, "[ ❌ ] Wyslana przez ciebie propozycja ma zablokowane slowa");


}
