package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.user.entity.User;
import com.user.repository.UserRepository;

@Component
@Profile("dev")
public class DefaultAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultAdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean bootstrapEnabled;
    private final String bootstrapUsername;
    private final String bootstrapPassword;

    public DefaultAdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.bootstrap.enabled:false}") boolean bootstrapEnabled,
            @Value("${app.admin.bootstrap.username:}") String bootstrapUsername,
            @Value("${app.admin.bootstrap.password:}") String bootstrapPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapEnabled = bootstrapEnabled;
        this.bootstrapUsername = bootstrapUsername;
        this.bootstrapPassword = bootstrapPassword;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        if (!bootstrapEnabled) {
            log.info("Admin bootstrap is disabled for this dev environment.");
            return;
        }

        if (bootstrapUsername.isBlank() || bootstrapPassword.isBlank()) {
            throw new IllegalStateException(
                    "Admin bootstrap is enabled but APP_ADMIN_BOOTSTRAP_USERNAME or APP_ADMIN_BOOTSTRAP_PASSWORD is missing.");
        }

        if (userRepository.count() > 0) {
            log.info("Skipping admin bootstrap because users already exist.");
            return;
        }

        User adminUser = new User();
        adminUser.setUsername(bootstrapUsername);
        adminUser.setPasswordHash(passwordEncoder.encode(bootstrapPassword));
        adminUser.setRole("ROLE_ADMIN");
        userRepository.save(adminUser);

        log.warn("Bootstrapped a development admin user {}. Disable APP_ADMIN_BOOTSTRAP_ENABLED after first use.", bootstrapUsername);
    }
}
