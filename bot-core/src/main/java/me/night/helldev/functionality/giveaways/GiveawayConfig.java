package me.night.helldev.functionality.giveaways;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import cc.dreamcode.utilities.builder.ListBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import me.night.helldev.functionality.proposition.Proposition;

import java.util.List;

@Configuration(child = "giveaway.yml")
@Header("## Hell (Giveaway-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GiveawayConfig extends OkaeriConfig {

    public List<Giveaway> giveaways = new ListBuilder<Giveaway>().build();

}
