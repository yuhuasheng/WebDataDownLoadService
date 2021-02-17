package com.dayang.domain;

import lombok.Data;

import java.util.Objects;


public class ChemicalIndustryInfo {

    /**
     * 单位名称
     */
    private String companyName;

    /**
     * 企业性质
     */
    private String enterpriseNature;

    /**
     * 公司简介
     */
    private String companyProfile;

    /**
     * 省市
     */
    private String province;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 固话
     */
    private String telephone;

    /**
     * 手机
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEnterpriseNature() {
        return enterpriseNature;
    }

    public void setEnterpriseNature(String enterpriseNature) {
        this.enterpriseNature = enterpriseNature;
    }

    public String getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(String companyProfile) {
        this.companyProfile = companyProfile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalIndustryInfo that = (ChemicalIndustryInfo) o;
        return Objects.equals(companyName, that.companyName) &&
                Objects.equals(enterpriseNature, that.enterpriseNature) &&
                Objects.equals(companyProfile, that.companyProfile) &&
                Objects.equals(province, that.province) &&
                Objects.equals(address, that.address) &&
                Objects.equals(contacts, that.contacts) &&
                Objects.equals(telephone, that.telephone) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, enterpriseNature, companyProfile, province, address, contacts, telephone, phone, email);
    }
}
