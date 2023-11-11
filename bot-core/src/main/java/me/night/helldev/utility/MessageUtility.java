package me.night.helldev.utility;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.channel.AutoArchiveDuration;
import org.javacord.api.entity.message.Message;

@UtilityClass
public class MessageUtility {

    public void openDiscussion(Message message, String userName) {
        message.createThread("Dyskusja na temat propozycji uzytkownika: " + userName, AutoArchiveDuration.ONE_WEEK);
    }
}
