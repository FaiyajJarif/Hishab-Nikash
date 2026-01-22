package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.MyInvitationDto;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.FamilyInvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family/invitations")
public class FamilyInvitationController {

    private final AuthUserService authUserService;
    private final FamilyInvitationService invitationService;

    public FamilyInvitationController(
            AuthUserService authUserService,
            FamilyInvitationService invitationService
    ) {
        this.authUserService = authUserService;
        this.invitationService = invitationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MyInvitationDto>>> myInvites(
            @RequestHeader("Authorization") String auth
    ) {
        String email = authUserService.requireEmail(auth);
        return ResponseEntity.ok(ApiResponse.ok(invitationService.getMyPendingInvites(email)));
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Void>> accept(
            @RequestHeader("Authorization") String auth,
            @RequestParam String token
    ) {
        Integer userId = authUserService.requireUserId(auth);
        String email = authUserService.requireEmail(auth);
        invitationService.accept(token, userId, email);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @RequestHeader("Authorization") String auth,
            @RequestParam String token
    ) {
        Integer userId = authUserService.requireUserId(auth);
        String email = authUserService.requireEmail(auth);
        invitationService.reject(token, userId, email);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
