package com.Eqinox.store.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "category_goals",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"category_id", "month", "year"}
       ))
public class CategoryGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(nullable = false)
    private Integer month; // 1–12

    @Column(nullable = false)
    private Integer year;

    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount; // monthly normalized

    @Column(nullable = false)
    private String frequency; // DAILY / WEEKLY / MONTHLY / YEARLY
    @Column(nullable = true)
    private BigDecimal totalTargetAmount;

    // getters & setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    public Integer getMonth() {
        return month;
    }
    public void setMonth(Integer month) {
        this.month = month;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
    public String getFrequency() {
        return frequency;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public BigDecimal getTotalTargetAmount() {
        return totalTargetAmount;
    }
    
    public void setTotalTargetAmount(BigDecimal totalTargetAmount) {
        this.totalTargetAmount = totalTargetAmount;
    }    
}
