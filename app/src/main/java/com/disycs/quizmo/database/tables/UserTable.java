package com.disycs.quizmo.database.tables;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by amine on 07/06/15.
 */
public class UserTable implements BaseColumns {

    public static final String USER_TABLE = "User";
    public static final String USER_NAME = _ID;
    public static final String PASSWORD = "password";


    private static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + " ( " +
            USER_NAME + " TEXT PRIMARY KEY NOT NULL," +
            PASSWORD + " TEXT NOT NULL)";





    public static final String CONTENT_PATH = "user";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.mastermind.user";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.mastermind.user";

    public static final String[] PROJECTION_ALL = { _ID, USER_NAME, PASSWORD };

    /**
     * create User table
     *
     * @param database
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_USER_TABLE);
    }

    /**
     * upgrade the User table
     *
     * @param database
     * @param oldVersion
     * @param newVersion
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        // TODO
        database.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(database);
    }
}
