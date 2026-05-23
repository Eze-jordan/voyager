package com.solutechOne.voyager.dto;

public class KycResponse {

    private String errorMessage;
    private String full_name;
    private String customer_account_number;
    private boolean is_active;

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getCustomer_account_number() { return customer_account_number; }
    public void setCustomer_account_number(String customer_account_number) { this.customer_account_number = customer_account_number; }

    public boolean isIs_active() { return is_active; }
    public void setIs_active(boolean is_active) { this.is_active = is_active; }

}