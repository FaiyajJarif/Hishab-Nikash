package com.Eqinox.store.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Eqinox.store.entities.FamilyGroup;

public interface FamilyGroupRepository
        extends JpaRepository<FamilyGroup, Integer> {

    List<FamilyGroup> findByOwnerId(Integer ownerId);
}

