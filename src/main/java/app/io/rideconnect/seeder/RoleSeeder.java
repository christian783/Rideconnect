package app.io.rideconnect.seeder;

import app.io.rideconnect.entity.Role;
import app.io.rideconnect.entity.enums.UserRole;
import app.io.rideconnect.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleSeeder {

    private final RoleRepository roleRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void seedRoles() {
        log.info("Starting role seeding AFTER JPA initialization...");

        Arrays.stream(UserRole.values())
                .filter(role -> roleRepository.findByName(role).isEmpty())
                .map(Role::new)
                .forEach(roleRepository::save);

        log.info("Role seeding completed. {} roles in database.", roleRepository.count());
    }
}
