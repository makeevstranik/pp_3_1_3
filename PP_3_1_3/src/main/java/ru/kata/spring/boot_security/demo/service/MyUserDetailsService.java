package ru.kata.spring.boot_security.demo.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class MyUserDetailsService implements UserDetailsService {
    private  UserRepository userRepository;
    private  PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setPersonRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            Hibernate.initialize(user.get().getRoles()); // get lazy roles
            return user.get();
        } else {
            throw new UsernameNotFoundException("user not found");
        }
    }

    public Boolean isUserExistByEmail(String email, Long id) {
        Optional<User> user =  userRepository.findUserByEmail(email);
        if (id == null) return user.isPresent(); // for new user from form (id == null)
        if (user.isEmpty()) return false;
        return ! Objects.equals(user.get().getId(), id); // the same user must be validated
    }


    public List<User> getAllUsers() {
        return  userRepository.findAll();
    }
    public Optional<User> getUserById(Long id) { return userRepository.findUserById(id); }

    @Transactional
    public void registration(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    @Transactional
    public void updateUser(User user) {

        Optional<User> userFromDB = userRepository.findUserById(user.getId());
        if(userFromDB.isEmpty()) { return; }
        if (user.getPassword().isEmpty()) { // password has NOT been changed in index.html
            user.setPassword(userFromDB.get().getPassword()); // old pass without encoding
        } else { // password has been changed in index.html
            user.setPassword(passwordEncoder.encode(user.getPassword())); // set new encoding pass
        }
        userRepository.save(user);
    }
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
