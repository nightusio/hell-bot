package me.night.stormcity.listener;

import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import me.night.stormcity.config.BotConfig;
import me.night.stormcity.config.MessageConfig;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
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

        if (serverOptional.isEmpty()) return;

        if (event.getButtonInteraction().getCustomId().equals("verify")) {
            Optional<Role> optionalRole = event.getApi().getRoleById(botConfig.roleId);

            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();

                user.addRole(role);
                event.getInteraction().createImmediateResponder().setContent(messageConfig.verifiedSuccess).setFlags(MessageFlag.EPHEMERAL).respond();
            }
        }
    }
}
