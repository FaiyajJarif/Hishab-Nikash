package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.FamilyInvitation;
import com.Eqinox.store.entities.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface FamilyInvitationRepository extends JpaRepository<FamilyInvitation, Long> {

    Optional<FamilyInvitation> findByToken(String token);

    List<FamilyInvitation> findByInvitedEmailAndStatusOrderByCreatedAtDesc(String email, InvitationStatus status);

    List<FamilyInvitation> findByFamilyIdAndStatus(Integer familyId, InvitationStatus status);

    List<FamilyInvitation> findByStatusAndExpiresAtBefore(InvitationStatus status, OffsetDateTime now);
}
