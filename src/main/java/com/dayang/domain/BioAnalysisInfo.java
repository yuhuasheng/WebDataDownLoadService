package com.dayang.domain;

import lombok.Data;

import java.util.Objects;


public class BioAnalysisInfo {

    /**
     * 公司名称
     */
    private String company;

    /**
     * 主营
     */
    private String major;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 手机
     */
    private String mobilePhone;

    /**
     * 邮箱
     */
    private String email;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
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
        if (!super.equals(o)) return false;
        BioAnalysisInfo info = (BioAnalysisInfo) o;
        return Objects.equals(company, info.company) &&
                Objects.equals(major, info.major) &&
                Objects.equals(contacts, info.contacts) &&
                Objects.equals(phone, info.phone) &&
                Objects.equals(mobilePhone, info.mobilePhone) &&
                Objects.equals(email, info.email);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), company, major, contacts, phone, mobilePhone, email);
    }
}
