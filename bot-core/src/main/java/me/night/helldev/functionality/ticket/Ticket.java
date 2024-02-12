package me.night.helldev.functionality.ticket;

import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Data
@Getter
public class Ticket {

    private int id;
    private long userId;
    private String category;
    private List<Long> addedUsers;
    private long channelId;
    private long server;
    private long messageId;

    public Ticket(int id, long userId, String category, long server, List<Long> addedUsers, long channelId, long messageId) {
        this.id = id;
        this.server = server;
        this.userId = userId;
        this.category = category;
        this.addedUsers = addedUsers;
        this.channelId = channelId;
        this.messageId = messageId;
    }
}
