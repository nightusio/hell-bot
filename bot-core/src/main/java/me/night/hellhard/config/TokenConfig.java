package me.night.hellhard.config;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

@Configuration(child = "token.yml")
@Header("## StormCity (Token) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TokenConfig extends OkaeriConfig {

    @Comment("Jaki token bot ma obslugiwac?")
    public String token = "MTE2MzA2NzU2NDQ3ODQzOTUyNQ.GlD0aa.gMCtd5v2SEPD1bUprtFjqoNASF7utaIM99t0Z0";
}
