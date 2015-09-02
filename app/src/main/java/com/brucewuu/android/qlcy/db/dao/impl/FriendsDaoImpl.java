package com.brucewuu.android.qlcy.db.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.brucewuu.android.qlcy.db.DBHelper;
import com.brucewuu.android.qlcy.db.dao.FriendsDao;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.io.IOUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brucewuu on 15/4/23.
 */
public class FriendsDaoImpl implements FriendsDao {

    private static FriendsDaoImpl instance;
    private DBHelper dbHelper;
    private static final String TABLE = "friends";

    private FriendsDaoImpl() {
        dbHelper = DBHelper.getInstance();
    }

    public static FriendsDaoImpl getInstance() {
        if (instance == null) {
            synchronized (FriendsDaoImpl.class) {
                if (instance == null)
                    instance = new FriendsDaoImpl();
            }
        }

        return instance;
    }

    @Override
    public void saveAll(List<User> lists) {
        SQLiteDatabase db = dbHelper.openDatabase();
        try {
            db.beginTransaction();
            ContentValues values;
            for (User user : lists) {
                values = new ContentValues();
                values.put("id", user.getId());
                values.put("phone", user.getPhone());
                values.put("imtoken", user.getImtoken());
                values.put("nickname", user.getNickname());
                values.put("portraituri", user.getPortraituri());
                db.insert(TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public synchronized void save(User user) {
        SQLiteDatabase db = dbHelper.openDatabase();
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("phone", user.getPhone());
        values.put("imtoken", user.getImtoken());
        values.put("nickname", user.getNickname());
        values.put("portraituri", user.getPortraituri());

        db.insert(TABLE, null, values);
    }

    @Override
    public List<User> getFriends() {
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor = null;
        List<User> list = new ArrayList<>();
        try {
            db.beginTransaction();
            cursor = db.query(TABLE, null, null, null, null, null, null);
            User user;
            while (cursor.moveToNext()) {
                user = new User();
                user.setId(cursor.getString(cursor.getColumnIndex("id")));
                user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                user.setImtoken(cursor.getString(cursor.getColumnIndex("imtoken")));
                user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
                user.setPortraituri(cursor.getString(cursor.getColumnIndex("portraituri")));
                list.add(user);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            IOUtils.closeQuietly(cursor);
        }

        return list;
    }

    @Override
    public void delete(String id) {
        SQLiteDatabase db = dbHelper.openDatabase();
        db.execSQL("delete from friends where id=?", new String[]{id});
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.openDatabase();
        db.delete(TABLE, null, null);
    }
}