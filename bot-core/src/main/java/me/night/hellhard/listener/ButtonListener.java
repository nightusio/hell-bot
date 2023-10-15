package me.night.hellhard.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.hellhard.config.BotConfig;
import me.night.hellhard.config.MessageConfig;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ButtonListener implements ButtonClickListener {

    private final BotConfig botConfig;
    private final MessageConfig messageConfig;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        User user = event.getInteraction().getUser();
        Optional<Server> serverOptional = event.getInteraction().getServer();

        if (serverOptional.isEmpty()) {
            return;
        }

        if (event.getButtonInteraction().getCustomId().equals("verify")) {
            botConfig.roleIds.stream()
                    .map(roleId -> event.getApi().getRoleById(roleId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(user::addRole);

            event.getInteraction()
                    .createImmediateResponder()
                    .setContent(messageConfig.verifiedSuccess)
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }
}
