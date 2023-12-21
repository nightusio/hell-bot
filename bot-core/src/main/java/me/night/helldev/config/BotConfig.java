package me.night.helldev.config;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import cc.dreamcode.platform.persistence.StorageConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration(child = "config.yml")
@Header("## Hell (Main-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class BotConfig extends OkaeriConfig {
    @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wylaczyc. :P")
    public boolean debug = true;

    @Comment("Uzupelnij ponizsze menu danymi.")
    public StorageConfig storageConfig = new StorageConfig("helldev");

    @Comment("verify role ids")
    public List<String> roleIds = Arrays.asList("1169941084001030244");

    @Comment("proposition channel id")
    public String propositionChannelId = "1128341832435245188";

    public List<String> blockedWords = new ArrayList<>(
            Arrays.asList("@everyone", "@here"));

    @Comment("premium")
    public List<String> premiumRoleIds = Arrays.asList("1171878813261635625", "1171878894027157655");

    @Comment("discount 10")
    public List<String> discount10Ids = Arrays.asList("1171903991077150831", "1171904015727067306");


    @Comment("welcome channel id")
    public String welcomeChannelId = "1128265476557000855";

    @Comment("Amount of warns to ban user")
    public int maxWarns = 3;

    @Comment("Member amount channel (Voice Channels only)")
    public String memberCount = "1185974453218316359";

}
