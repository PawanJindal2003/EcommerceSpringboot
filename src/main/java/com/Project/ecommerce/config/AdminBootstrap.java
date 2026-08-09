package com.Project.ecommerce.config;

import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        name = "app.seed.demo-data",
        havingValue = "false",
        matchIfMissing = true
)
public class AdminBootstrap implements ApplicationRunner {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public AdminBootstrap(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<User> existingAdmin = userRepository.findByEmail(email);

        if(roleRepository.findByAuthority("ADMIN") == null && roleRepository.findByAuthority("CUSTOMER") == null && roleRepository.findByAuthority("SELLER") == null){
            roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("SELLER"));
            roleRepository.save(new Role("CUSTOMER"));
        }

        if(existingAdmin.isEmpty()){
            User admin = new User();
            admin.setFirstName("Pawan");
            admin.setMiddleName("Kumar");
            admin.setLastName("Jindal");
            admin.setEmail(email);
            admin.setPassword(bCryptPasswordEncoder.encode(password));
            admin.setIsLocked(false);
            admin.setIsActive(true);
            admin.setRole(roleRepository.findByAuthority("ADMIN"));

            userRepository.save(admin);
        }
    }
}

