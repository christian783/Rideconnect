package app.io.rideconnect.service;

import app.io.rideconnect.entity.Role;
import app.io.rideconnect.entity.enums.UserRole;

import java.util.Set;

public interface RoleService {
    void initDefaultRoles();
    Role getRoleByName(UserRole userRole);
    Set<Role> getRolesByNames(Set<UserRole> userRoles);

    Role createRole(UserRole userRole);
}