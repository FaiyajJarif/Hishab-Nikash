package com.Eqinox.store.services;

import com.Eqinox.store.dtos.FamilyEventDto;
import com.Eqinox.store.repositories.FamilyMemberRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FamilySyncService {

    private final FamilyMemberRepository familyMemberRepo;
    private final FamilyNotificationService familyNotificationService;

    public FamilySyncService(
            FamilyMemberRepository familyMemberRepo,
            FamilyNotificationService familyNotificationService) {
        this.familyMemberRepo = familyMemberRepo;
        this.familyNotificationService = familyNotificationService;
    }

    // ✅ NEW — used by FamilyBudgetService
    public void broadcastFamilyEvent(
            Integer familyId,
            Integer actorUserId,
            String type,
            String message
    ) {
        FamilyEventDto dto = new FamilyEventDto(
                familyId,
                type,
                message,
                OffsetDateTime.now()
        );
        familyNotificationService.broadcast(familyId, dto);
    }

    // 🔁 KEEP — used by personal → family propagation
    public void broadcastUserChange(Integer userId, String type, String message) {
        List<Integer> familyIds = familyMemberRepo.findFamilyIdsByUserId(userId);

        for (Integer familyId : familyIds) {
            FamilyEventDto dto = new FamilyEventDto(
                    familyId,
                    type,
                    message,
                    OffsetDateTime.now()
            );
            familyNotificationService.broadcast(familyId, dto);
        }
    }
}
