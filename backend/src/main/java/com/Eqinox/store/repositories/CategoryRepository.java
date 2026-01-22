package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByUserId(Integer userId);

    Optional<Category> findByCategoryIdAndUserId(
            Integer categoryId,
            Integer userId);

    boolean existsByUserIdAndName(Integer userId, String name);
}
