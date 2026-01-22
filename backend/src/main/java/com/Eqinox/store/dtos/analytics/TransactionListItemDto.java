package com.Eqinox.store.dtos.analytics;

import com.Eqinox.store.entities.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionListItemDto {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate date;
    private String note;

    public TransactionListItemDto(Integer id, Integer categoryId, String categoryName,
                                  TransactionType type, BigDecimal amount, LocalDate date, String note) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public Integer getId() { return id; }
    public Integer getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
}
