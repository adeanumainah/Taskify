package com.uas.java1.Initial;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uas.java1.model.Role;
import com.uas.java1.model.User;
import com.uas.java1.repository.RoleRepository;
import com.uas.java1.repository.UserRepository;
import com.uas.java1.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;

@Component
public class InitialDataLoader {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void loadInitialData() {
        if (roleRepository.findByRoleName("ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ADMIN", true));
        }
        if (roleRepository.findByRoleName("USER").isEmpty()) {
            roleRepository.save(new Role(null, "USER", true));
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
            User adminUser = User.builder()
                    .username("admin")
                    .password(PasswordUtil.hash("admin123"))
                    .email("dujanahsr07@gmail.com")
                    .namaLengkap("abu dujanah siregar")
                    .status(true)
                    .tanggalDibuat(LocalDate.now())
                    .tanggalPembaruan(LocalDate.now())
                    .roles(List.of(adminRole))
                    .build();
            userRepository.save(adminUser);
        }
    }
}
