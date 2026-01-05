package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.UserOnboardingSelection;

import java.util.Optional;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingSelectionRepository
        extends JpaRepository<UserOnboardingSelection, Integer> {

    void deleteByUserIdAndStep(Integer userId, String step);

    Optional<UserOnboardingSelection> findTopByUserIdAndStepAndCategory(
            Integer userId,
            String step,
            String category);

    Optional<UserOnboardingSelection> findTopByUserIdOrderByIdDesc(Integer userId);
    List<UserOnboardingSelection> findByUserId(Integer userId);

    List<UserOnboardingSelection> findByUserIdAndCategory(
            Integer userId,
            String category
    );

}
