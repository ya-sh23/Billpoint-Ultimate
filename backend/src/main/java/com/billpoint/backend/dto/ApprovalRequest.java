package com.billpoint.backend.dto;

public class ApprovalRequest {
    private String planName;
    private int validMonths;

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public int getValidMonths() { return validMonths; }
    public void setValidMonths(int validMonths) { this.validMonths = validMonths; }
}
