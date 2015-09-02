package com.brucewuu.android.qlcy.db;

import com.brucewuu.android.qlcy.db.dao.FriendsDao;
import com.brucewuu.android.qlcy.db.dao.impl.FriendsDaoImpl;

/**
 * DB静态工厂类
 * @author brucewuu
 */
public class DBDaoFactory {

    public static FriendsDao getFriendsDao() {
        return FriendsDaoImpl.getInstance();
    }
}