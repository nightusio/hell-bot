package me.night.helldev.utility;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.stream.StreamSupport;

@UtilityClass
public class RoleUtility {

    public static boolean hasHigherRole(User userToCheck, User targetUser, Server server) {
        int highestRolePositionToCheck = getHighestRolePosition(userToCheck.getRoles(server));
        int highestRolePositionTarget = getHighestRolePosition(targetUser.getRoles(server));

        return highestRolePositionToCheck > highestRolePositionTarget;
    }

    private static int getHighestRolePosition(Iterable<Role> roles) {
        return StreamSupport.stream(roles.spliterator(), false)
                .mapToInt(Role::getPosition)
                .max()
                .orElse(0);
    }

}
