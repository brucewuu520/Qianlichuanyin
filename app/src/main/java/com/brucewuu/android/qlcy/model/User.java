package com.brucewuu.android.qlcy.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by brucewuu on 15/9/1.
 */
public class User implements Serializable {

    @SerializedName("userid")
    private String id;
    private String phone;
    private String imtoken;
    private String nickname;
    private String portraituri;
    private String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImtoken() {
        return imtoken;
    }

    public void setImtoken(String imtoken) {
        this.imtoken = imtoken;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortraituri() {
        return portraituri;
    }

    public void setPortraituri(String portraituri) {
        this.portraituri = portraituri;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static User parseJson(String json) {
        return new Gson().fromJson(json, User.class);
    }

    public static String toJson(User user) {
        return new Gson().toJson(user);
    }
}
