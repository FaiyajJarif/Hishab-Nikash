package com.Eqinox.store.repositories.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FamilyExpenseView {
    Integer getId();
    BigDecimal getAmount();
    String getNote();
    LocalDateTime getSpentAt();
    String getCategoryName();
    String getUserName();
    String getFamilyName();
}
