package org.linlinjava.litemall.wx.dto;

public class PlatformAccount {

    private String platformName;
    private String platformPhone;
    private Double availableAmount;
    private Double totalAmount;
    private Double unAvailableAmount;
    private Double usedAmount;
    private Double dueAmount;
    private String realName;
    private String idCardNumber;
    private String mobile;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    private Integer accountId;

    public String getPlatformName() {
        return platformName;
    }

    public String getPlatformPhone() {
        return platformPhone;
    }

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Double getUnAvailableAmount() {
        return unAvailableAmount;
    }

    public Double getUsedAmount() {
        return usedAmount;
    }

    public Double getDueAmount() {
        return dueAmount;
    }

    public String getRealName() {
        return realName;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setPlatformName (String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName (String platformName) {
        return this.platformName;
    }

    public void setPlatformPhone (String platformPhone) {
        this.platformPhone = platformPhone;
    }

    public void setAvailableAmount (Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setTotalAmount (Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setUnAvailableAmount (Double unAvailableAmount) {
        this.unAvailableAmount = unAvailableAmount;
    }

    public void setUsedAmount (Double usedAmount) {
        this.usedAmount = usedAmount;
    }

    public void setDueAmount (Double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public void setRealName (String realName) {
        this.realName = realName;
    }

    public void setIdCardNumber (String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public void setMobile (String mobile) {
        this.mobile = mobile;
    }
}
