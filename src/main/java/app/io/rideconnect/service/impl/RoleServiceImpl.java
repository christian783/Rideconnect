package app.io.rideconnect.service.impl;

import app.io.rideconnect.entity.Role;
import app.io.rideconnect.entity.enums.UserRole;
import app.io.rideconnect.repository.RoleRepository;
import app.io.rideconnect.service.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initDefaultRoles() {
        for (UserRole userRole : UserRole.values()) {
            if (!roleRepository.existsByName(userRole)) {
                Role role = new Role();
                role.setName(userRole);
                roleRepository.save(role);
                log.info("Created role: {}", userRole);
            }
        }
        log.info("All default roles are initialized");
    }

    public Role getRoleByName(UserRole userRole) {
        return roleRepository.findByName(userRole)
                .orElseGet(() -> createRole(userRole));
    }

    public Set<Role> getRolesByNames(Set<UserRole> userRoles) {
        return userRoles.stream()
                .map(this::getRoleByName)
                .collect(Collectors.toSet());
    }

    public Role createRole(UserRole userRole) {
        Role role = new Role();
        role.setName(userRole);
        return roleRepository.save(role);
    }
}