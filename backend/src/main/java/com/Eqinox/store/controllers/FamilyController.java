package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.CreateFamilyRequest;
import com.Eqinox.store.dtos.FamilyMemberDto;
import com.Eqinox.store.dtos.InviteFamilyRequest;
import com.Eqinox.store.dtos.InviteFamilyResponse;
import com.Eqinox.store.entities.FamilyGroup;
import com.Eqinox.store.entities.FamilyMember;
import com.Eqinox.store.entities.FamilyRole;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.FamilyInvitationService;
import com.Eqinox.store.services.FamilyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/family")
public class FamilyController {

    private final FamilyService familyService;
    private final FamilyInvitationService invitationService;
    private final AuthUserService authUserService;

    public FamilyController(
            FamilyService familyService,
            FamilyInvitationService invitationService,
            AuthUserService authUserService
    ) {
        this.familyService = familyService;
        this.invitationService = invitationService;
        this.authUserService = authUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FamilyGroup>> create(
            @RequestHeader("Authorization") String auth,
            @RequestBody CreateFamilyRequest req
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                familyService.createFamily(req.getName(), userId)
        ));
    }

    @PostMapping("/{familyId}/invite")
    public ResponseEntity<ApiResponse<InviteFamilyResponse>> invite(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @RequestBody InviteFamilyRequest req
    ) {
        Integer inviterId = authUserService.requireUserId(auth);
        InviteFamilyResponse res = invitationService.invite(familyId, inviterId, req.getEmail(), req.getRole());
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<FamilyMember>>> myFamilies(
            @RequestHeader("Authorization") String auth
    ) {
        Integer userId = authUserService.requireUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                familyService.getMyFamilies(userId)
        ));
    }

    @GetMapping("/{familyId}/members")
        public ResponseEntity<ApiResponse<List<FamilyMemberDto>>> members(
                @RequestHeader("Authorization") String auth,
                @PathVariable Integer familyId
        ) {
        Integer userId = authUserService.requireUserId(auth);

        return ResponseEntity.ok(
                ApiResponse.ok(
                familyService.getFamilyMembers(familyId, userId)
                )
        );
        }


    @DeleteMapping("/{familyId}/remove/{userId}")
    public ResponseEntity<ApiResponse<Void>> remove(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId,
            @PathVariable Integer userId
    ) {
        Integer adminId = authUserService.requireUserId(auth);
        familyService.removeMember(familyId, userId, adminId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{familyId}/leave")
    public ResponseEntity<ApiResponse<Void>> leave(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer familyId
    ) {
        Integer userId = authUserService.requireUserId(auth);
        familyService.leaveFamily(familyId, userId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PutMapping("/{familyId}/members/{userId}/role")
public ResponseEntity<ApiResponse<Void>> changeMemberRole(
        @RequestHeader("Authorization") String auth,
        @PathVariable Integer familyId,
        @PathVariable Integer userId,
        @RequestBody Map<String, String> body
) {
    Integer actorUserId = authUserService.requireUserId(auth);

    familyService.changeMemberRole(
            familyId,
            actorUserId,   // ✅ CORRECT USER (3)
            userId,
            body.get("role")
    );

    return ResponseEntity.ok(ApiResponse.ok());
}

}
