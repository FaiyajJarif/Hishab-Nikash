package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.FamilyActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyActivityRepository extends JpaRepository<FamilyActivity, Long> {

    List<FamilyActivity> findTop20ByFamilyIdOrderByCreatedAtDesc(Integer familyId);
}
