package me.night.helldev.functionality.ticket;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import cc.dreamcode.utilities.builder.ListBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import me.night.helldev.functionality.ticket.category.TicketCategory;

import java.util.List;

@Configuration(child = "ticket.yml")
@Header("## Hell (Ticket-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TicketConfig extends OkaeriConfig {
    public List<Ticket> tickets = new ListBuilder<Ticket>().build();

    public List<TicketCategory> ticketCategories = new ListBuilder<TicketCategory>().build();

}
