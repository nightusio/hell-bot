package me.night.helldev.functionality.ticket.category;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;

public class TicketCategorySerdes implements ObjectSerializer<TicketCategory> {

    @Override
    public boolean supports(@NonNull Class<? super TicketCategory> type) {
        return TicketCategory.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull TicketCategory object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("id", object.getId());
        data.add("buttonIDMenu", object.getButtonIDMenu());
        data.add("buttonClose", object.getButtonClose());
        data.add("buttonConfirmClose", object.getButtonConfirmClose());
        data.add("buttonDelete", object.getButtonDelete());
        data.add("ticketName", object.getTicketName());
        data.add("categoryId", object.getCategoryId());
        data.add("name", object.getName());
        data.add("description", object.getDescription());
        data.add("emoji", object.getEmoji());

    }

    @Override
    public TicketCategory deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        TicketCategory ticketCategory = new TicketCategory(
                data.get("id", String.class),
                data.get("categoryId", long.class),
                data.get("name", String.class),
                data.get("description", String.class),
                data.get("emoji", String.class)
        );

        ticketCategory.setButtonIDMenu(data.get("buttonIDMenu", String.class));
        ticketCategory.setButtonClose(data.get("buttonClose", String.class));
        ticketCategory.setButtonConfirmClose(data.get("buttonConfirmClose", String.class));
        ticketCategory.setButtonDelete(data.get("buttonDelete", String.class));
        ticketCategory.setTicketName(data.get("ticketName", String.class));
        return ticketCategory;
    }
}
