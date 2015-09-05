package com.brucewuu.android.qlcy.model;

import com.brucewuu.android.qlcy.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吴昭 on 2015-9-5.
 */
public class GroupInfo {

    private String groupid;
    private String groupname;
    private String groupicon;
    private String createuserid;
    private List<User> userlist;

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupicon() {
        return groupicon;
    }

    public void setGroupicon(String groupicon) {
        this.groupicon = groupicon;
    }

    public String getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(String createuserid) {
        this.createuserid = createuserid;
    }

    public List<User> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<User> userlist) {
        this.userlist = userlist;
    }

    public static List<GroupInfo> getGroups(String json) {
        if (!StringUtils.isJsonArray(json))
            return null;
        Type listType = new TypeToken<ArrayList<GroupInfo>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }
}
