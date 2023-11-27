package me.night.helldev.functionality.ticket.category;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TicketCategory {

    private String id;
    private String buttonIDMenu;
    private String buttonClose;
    private String buttonConfirmClose;
    private String buttonDelete;
    private String ticketName;
    private long categoryId;
    private String name;
    private String description;
    private String emoji;

    public TicketCategory(String id, long categoryId, String name, String description, String emoji) {
        String idFixed = id.toLowerCase().replaceAll(" ", "");
        this.id = idFixed;
        this.buttonIDMenu = "ticket_" + idFixed;
        this.buttonClose = "ticket_" + idFixed + "_close";
        this.buttonConfirmClose = "ticket_" + idFixed + "_confirmClose";
        this.buttonDelete = "ticket_" + idFixed + "_delete";
        this.ticketName = idFixed + "-";
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
    }
}
