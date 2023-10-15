package me.night.hellhard.config;

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

@Configuration(child = "messages.yml")
@Headers({
        @Header("## StormCity (Message-Config) ##"),
        @Header("Dostepne type: (MESSAGE, EMBED)")
})
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MessageConfig extends OkaeriConfig {

    public Notice reloadFailed = new Notice(NoticeType.MESSAGE, "``[ ❌ ] Wystapil problem z przeladowanie konfiguracji! ({reason})``");
    public Notice reloadApplied = new Notice(NoticeType.MESSAGE, "``[ ✅ ] Przeladowano konfiguracje! ({time})``");
    @Comment("/embed")
    public Notice embedCommand = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("{title}")
            .setDescription("{message}")
            .setFooter("HellHard | 2023")
            .setAuthor("{bot}")
            .setImage("https://cdn.discordapp.com/attachments/1157576834108641304/1162828182253207673/HellHard-Logo.png?ex=653d5b22&is=652ae622&hm=acf50c1acf2fff46074b588f3028300a3fabf268362f166021104edb5d027955&")
            .setTimestampToNow());

    public Notice embedCommandSent = new Notice(NoticeType.MESSAGE, "[ ✅ ] Wyslano wiadomosc na kanal: <#{channel}>");

    public String ticketCreated = "[ ✅ ] Pomyslnie stworzyles ticket! <#{channel}>!";

    public String verifiedSuccess = "[ ✅ ] Pomyslnie sie zweryfikowales!";

    @Comment("Propozycja")
    public Notice proposition = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("Propozycja uzytkownika: {user}")
            .setDescription("{message}")
            .setAuthor("{userAsUser}")
            .setFooter("HellHard | 2023")
            .setTimestampToNow());

    @Comment("Powitanie")
    public Notice welcome = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("Witaj {user}")
            .setDescription("Mamy nadzieje ze zostaniesz z nami na dluzej!")
            .setFooter("HellHard | 2023")
            .setTimestampToNow());


    public Notice propositionNotSend = new Notice(NoticeType.MESSAGE, "[ ❌ ] Wyslana przez ciebie propozycja ma zablokowane slowa");


}
