package com.Eqinox.store.dtos;

import java.math.BigDecimal;
import java.util.Map;

public class AllocateLeftoverRequest {
    // goalId -> amount
    private Map<Integer, BigDecimal> allocations;

    public Map<Integer, BigDecimal> getAllocations() { return allocations; }
    public void setAllocations(Map<Integer, BigDecimal> allocations) { this.allocations = allocations; }
}
