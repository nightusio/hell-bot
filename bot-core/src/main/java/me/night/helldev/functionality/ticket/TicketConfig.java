package me.night.helldev.functionality.ticket;

import cc.dreamcode.platform.javacord.component.configuration.Configuration;
import cc.dreamcode.utilities.builder.ListBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import me.night.helldev.functionality.ticket.Ticket;

import java.util.List;

@Configuration(child = "ticket.yml")
@Header("## Hell (Ticket-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TicketConfig extends OkaeriConfig {
    public List<Ticket> tickets = new ListBuilder<Ticket>().build();

    @Comment("Id kategorii od ticketow")
    public String ticketCategoryId = "1127226910053769266";

    @Comment("Categories: USLUGA")
    public String uslugaButtonIDMenu = "ticket_usluga";
    public String uslugaButtonClose = "ticket_usluga_close";
    public String uslugaButtonConfirmClose = "ticket_usluga_confirmClose";
    public String uslugaButtonDelete = "ticket_usluga_delete";
    public String uslugaTicketName = "usluga-";

    @Comment("Categories: POMOC")
    public String pomocButtonIDMenu = "ticket_pomoc";
    public String pomocButtonClose = "ticket_pomoc_close";
    public String pomocButtonConfirmClose = "ticket_pomoc_confirmClose";
    public String pomocButtonDelete = "ticket_pomoc_delete";
    public String pomocTicketName = "pomoc-";

    @Comment("Categories: PARTNERSTWO")
    public String partnerstwoButtonIDMenu = "ticket_partnerstwo";
    public String partnerstwoButtonClose = "ticket_partnerstwo_close";
    public String partnerstwoButtonConfirmClose = "ticket_partnerstwo_confirmClose";
    public String partnerstwoButtonDelete = "ticket_partnerstwo_delete";
    public String partnerstwoTicketName = "partnerstwo-";

    @Comment("Categories: INNE")
    public String inneButtonIDMenu = "ticket_inne";
    public String inneButtonClose = "ticket_inne_close";
    public String inneButtonConfirmClose = "ticket_inne_confirmClose";
    public String inneButtonDelete = "ticket_inne_delete";
    public String inneTicketName = "inne-";

}
