package com.Eqinox.store.services;

import com.Eqinox.store.entities.FamilyActivity;
import com.Eqinox.store.repositories.FamilyActivityRepository;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FamilyActivityService {

    private final FamilyActivityRepository repo;

    public FamilyActivityService(FamilyActivityRepository repo) {
        this.repo = repo;
    }

    @Async("familyExecutor") // 🔥 THREADING
    public void log(Integer familyId, Integer userId, String action, String description) {

        FamilyActivity activity = new FamilyActivity();
        activity.setFamilyId(familyId);
        activity.setActorUserId(userId);
        activity.setAction(action);
        activity.setDescription(description);

        repo.save(activity);
    }
}
