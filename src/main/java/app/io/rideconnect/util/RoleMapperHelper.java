package app.io.rideconnect.util;
import app.io.rideconnect.entity.Role;

import app.io.rideconnect.entity.enums.UserRole;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleMapperHelper {

    @Named("userRolesToRoleNames")
    public Set<UserRole> userRolesToRoleNames(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Named("userRoleNamesToRoles")
    public Set<Role> userRoleNamesToRoles(Set<UserRole> roleNames) {
        if (roleNames == null) return null;
        return roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
