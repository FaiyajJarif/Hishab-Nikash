package com.Eqinox.store.services;

import com.Eqinox.store.dtos.AllocateLeftoverRequest;
import com.Eqinox.store.entities.Goal;
import com.Eqinox.store.entities.GoalContribution;
import com.Eqinox.store.repositories.GoalContributionRepository;
import com.Eqinox.store.repositories.GoalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class GoalService {

    private final GoalRepository goalRepo;
    private final GoalContributionRepository contribRepo;

    public GoalService(GoalRepository goalRepo, GoalContributionRepository contribRepo) {
        this.goalRepo = goalRepo;
        this.contribRepo = contribRepo;
    }

    public List<Goal> getActiveGoals(Integer userId) {
        return goalRepo.findByUserIdAndActiveTrue(userId);
    }

    @Transactional
    public Goal createGoal(Integer userId, String name, BigDecimal target, String note) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Goal name required");
        if (target == null || target.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Target must be > 0");

        Goal g = new Goal();
        g.setUserId(userId);
        g.setName(name);
        g.setTargetAmount(target);
        g.setNote(note);
        return goalRepo.save(g);
    }

    @Transactional
    public void allocateLeftover(Integer userId, int month, int year, BigDecimal leftover, AllocateLeftoverRequest req) {
        if (leftover == null || leftover.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("No leftover to allocate");
        }
        if (req == null || req.getAllocations() == null || req.getAllocations().isEmpty()) {
            throw new IllegalArgumentException("Allocations required");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal a : req.getAllocations().values()) {
            if (a == null || a.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Allocation cannot be negative");
            total = total.add(a);
        }

        if (total.compareTo(leftover) > 0) {
            throw new IllegalStateException("Allocations exceed leftover");
        }

        for (Map.Entry<Integer, BigDecimal> e : req.getAllocations().entrySet()) {
            Integer goalId = e.getKey();
            BigDecimal amount = e.getValue();

            if (amount.compareTo(BigDecimal.ZERO) == 0) continue;

            Goal g = goalRepo.findByGoalIdAndUserId(goalId, userId)
                    .orElseThrow(() -> new RuntimeException("Goal not found: " + goalId));

            g.setCurrentAmount(g.getCurrentAmount().add(amount));
            goalRepo.save(g);

            GoalContribution c = new GoalContribution();
            c.setUserId(userId);
            c.setGoalId(goalId);
            c.setMonth(month);
            c.setYear(year);
            c.setAmount(amount);
            contribRepo.save(c);
        }
    }
}
