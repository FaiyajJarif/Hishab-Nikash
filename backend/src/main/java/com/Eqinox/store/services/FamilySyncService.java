package com.Eqinox.store.services;

import com.Eqinox.store.dtos.FamilyEventDto;
import com.Eqinox.store.entities.User;
import com.Eqinox.store.repositories.FamilyMemberRepository;
import com.Eqinox.store.repositories.UserRepository;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FamilySyncService {

    private final FamilyMemberRepository familyMemberRepo;
    private final FamilyNotificationService familyNotificationService;
    private final UserRepository userRepository;

    public FamilySyncService(
            FamilyMemberRepository familyMemberRepo,
            FamilyNotificationService familyNotificationService,
            UserRepository userRepository) {
        this.familyMemberRepo = familyMemberRepo;
        this.familyNotificationService = familyNotificationService;
        this.userRepository = userRepository;
    }

    // ✅ NEW — used by FamilyBudgetService
    public void broadcastFamilyEvent(
            Integer familyId,
            Integer actorUserId,
            String type,
            String message
    ) {
        String actorName = userRepository.findById(actorUserId)
            .map(User::getName)
            .orElse("Someone");

        FamilyEventDto dto = new FamilyEventDto(
            familyId,
            type,
            message,
            actorName,                // ✅ name, not id
            OffsetDateTime.now()
    );
        familyNotificationService.broadcast(familyId, dto);
    }

    // 🔁 KEEP — used by personal → family propagation
    public void broadcastUserChange(Integer userId, String type, String message) {
        List<Integer> familyIds = familyMemberRepo.findFamilyIdsByUserId(userId);
    
        String actorName = userRepository.findById(userId)
                .map(User::getName)
                .orElse("Someone");
    
        for (Integer familyId : familyIds) {
            FamilyEventDto dto = new FamilyEventDto(
                    familyId,
                    type,
                    message,
                    actorName,          // ✅ ADD THIS
                    OffsetDateTime.now()
            );
            familyNotificationService.broadcast(familyId, dto);
        }
    }
    
}
