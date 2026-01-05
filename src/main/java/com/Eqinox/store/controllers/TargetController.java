package com.Eqinox.store.controllers;

import com.Eqinox.store.entities.Target;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.TargetRepository;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.security.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/targets")
public class TargetController {

    private final TargetRepository targetRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    public TargetController(TargetRepository targetRepo, UserRepository userRepo, JwtService jwtService) {
        this.targetRepo = targetRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @PostMapping
    public void saveTarget(
            @RequestHeader("Authorization") String auth,
            @RequestBody Target target) {

        String email = jwtService.getEmail(auth.substring(7));
        User user = userRepo.findByEmail(email).orElseThrow();

        target.setUserId(user.getUserId());
        targetRepo.save(target);
    }

    @GetMapping
    public Object getTargets(@RequestHeader("Authorization") String auth) {
        String email = jwtService.getEmail(auth.substring(7));
        User user = userRepo.findByEmail(email).orElseThrow();
        return targetRepo.findByUserId(user.getUserId());
    }
}
