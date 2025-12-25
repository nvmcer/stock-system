package com.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.user.repository.UserRepository;

@Component
public class DefaultAdminInitializer implements ApplicationRunner{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws Exception {
        if (userRepository.count() == 0) {
            var adminUser = new com.user.entity.User();
            adminUser.setUsername("admin");
            adminUser.setPasswordHash(passwordEncoder.encode("admin123"));
            adminUser.setRole("ROLE_ADMIN");
            userRepository.save(adminUser);

            System.out.println("Default admin user created.");
        }
    }
}
