package com.brucewuu.android.qlcy.db.dao;


import com.brucewuu.android.qlcy.model.User;

import java.util.List;

/**
 * Created by brucewuu on 15/4/23.
 */
public interface FriendsDao {

    void saveAll(List<User> lists);

    void save(User user);

    List<User> getFriends();

    void delete(String id);

    void deleteAll();
}
