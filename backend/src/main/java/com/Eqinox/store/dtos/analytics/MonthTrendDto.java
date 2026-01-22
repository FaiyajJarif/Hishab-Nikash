package com.Eqinox.store.dtos.analytics;

import java.math.BigDecimal;
import java.util.List;

public class MonthTrendDto {
    private List<String> labels;
    private List<BigDecimal> values;

    public MonthTrendDto(List<String> labels, List<BigDecimal> values) {
        this.labels = labels;
        this.values = values;
    }

    public List<String> getLabels() { return labels; }
    public List<BigDecimal> getValues() { return values; }
}
