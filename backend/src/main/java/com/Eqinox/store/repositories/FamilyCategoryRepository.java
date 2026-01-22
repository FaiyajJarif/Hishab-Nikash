package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.FamilyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyCategoryRepository extends JpaRepository<FamilyCategory, Integer> {
    List<FamilyCategory> findByFamilyId(Integer familyId);
}
