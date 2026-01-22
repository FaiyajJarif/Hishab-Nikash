package com.Eqinox.store.controllers;

import com.Eqinox.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbCheckController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/db-check")
    public String checkDb() {
        long count = userRepository.count(); // runs: SELECT COUNT(*) FROM users
        return "DB is OK. Users in table: " + count;
    }
}
