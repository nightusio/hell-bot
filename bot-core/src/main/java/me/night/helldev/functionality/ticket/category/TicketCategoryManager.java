package me.night.helldev.functionality.ticket.category;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.helldev.functionality.ticket.Ticket;
import me.night.helldev.functionality.ticket.TicketConfig;
import me.night.helldev.functionality.ticket.exception.TicketError;
import me.night.helldev.functionality.ticket.exception.TicketException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TicketCategoryManager {

    private final TicketConfig ticketConfig;

    public void createTicketCategory(TicketCategory ticketCategory) {
        ticketConfig.ticketCategories.add(ticketCategory);
        ticketConfig.save();
    }

    public void removeTicketCategory(TicketCategory ticketCategory) {
        ticketConfig.ticketCategories.remove(ticketCategory);
        ticketConfig.save();
    }

    public TicketCategory getTicketCategory(String identifier) throws TicketException {
        return ticketConfig.ticketCategories.stream()
                .filter(category -> String.valueOf(category.getCategoryId()).equalsIgnoreCase(identifier))
                .findFirst()
                .orElse(ticketConfig.ticketCategories.stream()
                        .filter(category -> category.getId().equalsIgnoreCase(identifier)
                                || category.getButtonIDMenu().equalsIgnoreCase(identifier)
                                || category.getButtonClose().equalsIgnoreCase(identifier)
                                || category.getButtonConfirmClose().equalsIgnoreCase(identifier)
                                || category.getButtonDelete().equalsIgnoreCase(identifier)
                                || category.getTicketName().equalsIgnoreCase(identifier))
                        .findFirst()
                        .orElseThrow(() -> new TicketException("Invalid Category")));
    }

    public TicketCategory getTicketCategoryByTicket(Ticket ticket) {
        return ticketConfig.ticketCategories.stream()
                .filter(category -> category.getId().equalsIgnoreCase(ticket.getCategory()))
                .findFirst()
                .orElseThrow(() -> new TicketError("Ticket category is not found!"));
    }

    public TicketCategory getCategoryById(String categoryId) throws TicketException {
        return ticketConfig.ticketCategories.stream()
                .filter(category -> String.valueOf(category.getCategoryId()).equalsIgnoreCase(categoryId))
                .findFirst()
                .orElseThrow(() -> new TicketException("Category not found with ID: " + categoryId));
    }


    public List<TicketCategory> getTicketCategories() {
        return new ArrayList<>(ticketConfig.ticketCategories);
    }
}
