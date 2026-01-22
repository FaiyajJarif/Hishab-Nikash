package com.Eqinox.store.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Eqinox.store.entities.FamilyMember;

public interface FamilyMemberRepository
        extends JpaRepository<FamilyMember, Integer> {

    Optional<FamilyMember> findByFamilyIdAndUserId(Integer familyId, Integer userId);

    List<FamilyMember> findByUserId(Integer userId);

    List<FamilyMember> findByFamilyId(Integer familyId);

    @Query("select fm.familyId from FamilyMember fm where fm.userId = :userId")
    List<Integer> findFamilyIdsByUserId(Integer userId);
}
