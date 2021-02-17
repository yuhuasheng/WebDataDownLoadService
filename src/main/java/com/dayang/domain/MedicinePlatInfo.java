package com.dayang.domain;

import lombok.Data;

import java.util.Objects;


public class MedicinePlatInfo {

    /**
     * 公司名称
     */
    private String company;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 主营
     */
    private String major;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 网址
     */
    private String url;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MedicinePlatInfo that = (MedicinePlatInfo) o;
        return Objects.equals(company, that.company) &&
                Objects.equals(address, that.address) &&
                Objects.equals(major, that.major) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), company, address, major, phone, email, url);
    }
}
