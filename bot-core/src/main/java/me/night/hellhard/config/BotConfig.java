package me.night.hellhard.config;

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
    public StorageConfig storageConfig = new StorageConfig("hellhard");

    @Comment("verify role ids")
    public List<String> roleIds = Arrays.asList("1157576832397348978", "1157576832397348979");

    @Comment("proposition channel id")
    public String propositionChannelId = "1157576833525620785";

    public List<String> blockedWords = new ArrayList<>(
            Arrays.asList("@everyone", "@here"));

    @Comment("welcome channel id")
    public String welcomeChannelId = "1157576833215254540";

    @Comment("ticket category")
    public String ticketCategoryID = "1163070567365234728";
}
