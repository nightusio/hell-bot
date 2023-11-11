package me.night.helldev.config;

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
        @Header("## Hell (Message-Config) ##"),
        @Header("Dostepne type: (MESSAGE, EMBED)")
})
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MessageConfig extends OkaeriConfig {


    public Notice maxLimitExceeded = new Notice(NoticeType.MESSAGE, "``[ ❌ ] Przekroczyles limit  !``");

    public Notice notNumber = new Notice(NoticeType.MESSAGE, "``[ ❌ ] Podana liczba nie jest !``");

    public Notice reloadFailed = new Notice(NoticeType.MESSAGE, "``[ ❌ ] Wystapil problem z przeladowanie konfiguracji! ({reason})``");
    public Notice reloadApplied = new Notice(NoticeType.MESSAGE, "``[ ✅ ] Przeladowano konfiguracje! ({time})``");
    @Comment("/embed")
    public Notice embedCommand = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("{title}")
            .setDescription("{message}")
            .setFooter("© 2023 HellDev.pl - ")
            .setTimestampToNow());

    @Comment("/clear")
    public Notice clearCommand = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setAuthor("HellDev - Clear")
            .setTitle("Usunieto {number} wiadomosci!")
            .setDescription("Pomyslnie wyczysciles {number} wiadomosci na kanele <#{channel}>!")
            .setFooter("© 2023 HellDev.pl - ")
            .setTimestampToNow());

    public Notice embedCommandSent = new Notice(NoticeType.MESSAGE, "[ ✅ ] Wyslano wiadomosc na kanal: <#{channel}>");

    public String ticketCreated = "[ ✅ ] Pomyslnie stworzyles ticket! <#{channel}>!";

    public String verifiedSuccess = "[ ✅ ] Pomyslnie sie zweryfikowales!";

    @Comment("Powitanie")
    public Notice welcome = new Notice(NoticeType.EMBED, new WrappedEmbedBuilder()
            .setTitle("Witaj {user}")
            .setDescription("Mamy nadzieje ze zostaniesz z nami na dluzej!")
            .setFooter("HellDev | 2023")
            .setImage("https://cdn.discordapp.com/attachments/1128260153834217533/1169681472689537104/hellcode-powitalnia.png?ex=655649c2&is=6543d4c2&hm=bdafaf7c9aa9e246decb9721b80b0062a7b7724b665dee6aac3505240b5c63b1&")
            .setTimestampToNow());


    public Notice propositionNotSend = new Notice(NoticeType.MESSAGE, "[ ❌ ] Wyslana przez ciebie propozycja ma zablokowane slowa");


}
