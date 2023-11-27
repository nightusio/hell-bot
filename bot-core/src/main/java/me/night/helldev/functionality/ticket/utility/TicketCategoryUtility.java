package me.night.helldev.functionality.ticket.utility;

import lombok.experimental.UtilityClass;
import me.night.helldev.functionality.ticket.exception.TicketError;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.server.Server;

@UtilityClass
public class TicketCategoryUtility {

    public ChannelCategory getServerCategoryById(Server server, long categoryId) {
        return server.getChannelCategories().stream()
                .filter(category -> category.getId() == categoryId)
                .findFirst()
                .orElseThrow(() -> new TicketError("Server category is not found!"));
    }
}
