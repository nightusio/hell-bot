package me.night.helldev.functionality.poll;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import cc.dreamcode.utilities.builder.ListBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import me.night.helldev.functionality.poll.Poll;

import java.util.List;

@Configuration(child = "poll.yml")
@Header("## Hell (Poll-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PollConfig extends OkaeriConfig {

    public List<Poll> polls = new ListBuilder<Poll>().build();

}
