package com.Eqinox.store.dtos;

import java.util.List;

public class OnboardingStepRequest {

    private String step;
    private List<SelectionDto> selections;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public List<SelectionDto> getSelections() {
        return selections;
    }

    public void setSelections(List<SelectionDto> selections) {
        this.selections = selections;
    }
}
