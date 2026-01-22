package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Integer> {
    List<Target> findByUserId(Integer userId);
    List<Target> findByUserIdAndCategoryId(Integer userId, Integer categoryId);
}
