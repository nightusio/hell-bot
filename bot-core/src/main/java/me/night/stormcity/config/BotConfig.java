package me.night.stormcity.config;

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
@Header("## StormCity (Main-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class BotConfig extends OkaeriConfig {
    @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wylaczyc. :P")
    public boolean debug = true;

    @Comment("Uzupelnij ponizsze menu danymi.")
    public StorageConfig storageConfig = new StorageConfig("stormcity");

    @Comment("verify role id")
    public String roleId = "1028656959148261437";

    public List<String> blockedWords = new ArrayList<>(
            Arrays.asList("@everyone", "@here"));

    @Comment("welcome channel id")
    public String welcomeChannelId = "1028660386209218641";

    @Comment("ticket category")
    public String ticketCategoryID = "1063542120406847621";
}
