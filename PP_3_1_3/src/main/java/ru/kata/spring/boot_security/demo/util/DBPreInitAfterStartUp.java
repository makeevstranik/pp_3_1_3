package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.Roles;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.MyUserDetailsService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.sql.SQLException;

@Component
public class DBPreInitAfterStartUp {

    private final MyUserDetailsService userDetailsService;
    private final RoleService roleService;
    @Autowired
    public DBPreInitAfterStartUp(MyUserDetailsService userDetailsService, RoleService roleService) {
        this.userDetailsService = userDetailsService;
        this.roleService = roleService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initDB() {
        Role userRole = new Role(Roles.ROLE_USER);
        Role adminRole = new Role(Roles.ROLE_ADMIN);
        roleService.save(userRole);
        roleService.save(adminRole);
        User testAdmin = new User("admin", "admin", 20, "admin@mail.ru", null);
        testAdmin.setPassword("1111");
        testAdmin.addRole(userRole);
        testAdmin.addRole(adminRole);
        userDetailsService.registration(testAdmin);
        System.out.println("DB init OK, testAdmin - name: admin, password: 1111, email: admin@mail.ru, Roles admin, user - OK");
    }
}
