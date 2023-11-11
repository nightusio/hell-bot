package me.night.helldev.functionality.ticket;

import lombok.Data;
import lombok.Getter;
import me.night.helldev.functionality.shared.SharedType;

@Data
@Getter
public class Ticket {

    private final long userID;
    private final SharedType sharedType;
    private final String channelID;

}
