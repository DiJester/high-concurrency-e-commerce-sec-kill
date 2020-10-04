package com.liudi.happyshopping.vo;

import com.liudi.happyshopping.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class SignupVo {

    @NotNull
    @Length(min = 4)
    private String nickname;

    @NotNull
    private Integer gender;

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 6)
    private String password;

    @Override
    public String toString() {
        return "SignupVo{" +
                "nickName='" + nickname +'\'' +
                ", gender='" + gender +'\'' +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String passWord) {
        this.password = passWord;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
}
