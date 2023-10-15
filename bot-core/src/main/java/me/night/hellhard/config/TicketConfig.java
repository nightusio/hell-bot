package me.night.hellhard.config;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import cc.dreamcode.utilities.builder.ListBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import me.night.hellhard.ticket.Ticket;

import java.util.List;

@Configuration(child = "ticket.yml")
@Header("## StormCity (Ticket-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TicketConfig extends OkaeriConfig {

    public boolean debug = false;
    public List<Ticket> tickets = new ListBuilder<Ticket>().build();

    @Comment("Categories: INNE")
    public String inneButtonIDMenu = "ticket_inne";
    public String inneButtonClose = "ticket_inne_close";
    public String inneButtonConfirmClose = "ticket_inne_confirmClose";
    public String inneButtonDelete = "ticket_inne_delete";
    public String inneTicketName = "inne-";

    @Comment("Categories: MINECRAFT")
    public String minecraftButtonIDMenu = "ticket_minecraft";
    public String minecraftButtonClose = "ticket_minecraft_close";
    public String minecraftButtonConfirmClose = "ticket_minecraft_confirmClose";
    public String minecraftButtonDelete = "ticket_minecraft_delete";
    public String minecraftTicketName = "minecraft-";

}
