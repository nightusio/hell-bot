package me.night.stormcity.ticket;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Ticket {

    private final long userID;
    private final TicketType ticketType;
    private final String channelID;
}
