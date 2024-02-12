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

    /**
     * /**
     * <p>
     * Creates a new ticket category and saves the updated configuration.
     *
     * @param ticketCategory The ticket category to be created.
     */
    public void createTicketCategory(TicketCategory ticketCategory) {
        ticketConfig.ticketCategories.add(ticketCategory);
        ticketConfig.save();
    }


    /**
     * Removes an existing ticket category and saves the updated configuration.
     *
     * @param ticketCategory The ticket category to be removed.
     */
    public void removeTicketCategory(TicketCategory ticketCategory) {
        ticketConfig.ticketCategories.remove(ticketCategory);
        ticketConfig.save();
    }


    /**
     * Retrieves a ticket category based on its identifier, which can be the category ID or other attributes.
     *
     * @param identifier The identifier of the ticket category to retrieve.
     * @return The corresponding ticket category.
     * @throws TicketException If no matching category is found.
     */
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


    /**
     * Retrieves the ticket category associated with a given ticket.
     *
     * @param ticket The ticket for which to retrieve the category.
     * @return The ticket category.
     * @throws TicketError If the associated category is not found.
     */
    public TicketCategory getTicketCategoryByTicket(Ticket ticket) {
        return ticketConfig.ticketCategories.stream()
                .filter(category -> category.getId().equalsIgnoreCase(ticket.getCategory()))
                .findFirst()
                .orElseThrow(() -> new TicketError("Ticket category is not found!"));
    }


    /**
     * Retrieves a ticket category based on its category ID.
     *
     * @param categoryId The category ID to search for.
     * @return The corresponding ticket category.
     * @throws TicketException If no matching category is found.
     */
//unchecked//
    public TicketCategory getCategoryById(String categoryId) throws TicketException {
        return ticketConfig.ticketCategories.stream()
                .filter(category -> String.valueOf(category.getCategoryId()).equalsIgnoreCase(categoryId))
                .findFirst()
                .orElseThrow(() -> new TicketException("Category not found with ID: " + categoryId));
    }



    /**
     * Retrieves a list of all ticket categories.
     *
     * @return The list of ticket categories.
     */

    public List<TicketCategory> getTicketCategories() {
        return new ArrayList<>(ticketConfig.ticketCategories);
    }
}
