package com.disycs.quizmo.database.tables;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by amine on 07/06/15.
 */
public class QuestionnaireTable implements BaseColumns {

    public static final String QUESTIONNAIRE_TABLE = "Questionnaire";
    public static final String ID_QUESTIONNAIRE = _ID;
    public static final String TITLE_QUESTIONNAIRE = "titQuest";
    public static final String DESC_QUESTIONNAIRE = "descQuest";
    public static final String DAT_CR_QUESTIONNAIRE = "datCrQuest";
    public static final String HASH_QUESTIONNAIRE = "hashQuest";
    public static final String STATE_QUESTIONNAIRE = "stateQuest";
    public static final String CAT_QUESTIONNAIRE = "categQuest";
    public static final String QUESTIONS = "Questions";
    private static final String CREATE_QUESTIONNAIRE_TABLE = "CREATE TABLE " + QUESTIONNAIRE_TABLE + " (" +
            ID_QUESTIONNAIRE + "	INT		PRIMARY KEY NOT NULL," +
            TITLE_QUESTIONNAIRE + "	TEXT 	NOT NULL," +
            DESC_QUESTIONNAIRE + "	TEXT	," +
            DAT_CR_QUESTIONNAIRE + " TEXT	," +
            HASH_QUESTIONNAIRE + "	TEXT 	NOT NULL," +
            STATE_QUESTIONNAIRE + " TEXT	," +
            CAT_QUESTIONNAIRE + " TEXT	," +
            QUESTIONS + " TEXT );";


    public static final String CONTENT_PATH = "questionnaire";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.mastermind.questionnaire";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.mastermind.questionnaire";

    public static final String[] PROJECTION_ALL = {_ID, ID_QUESTIONNAIRE, TITLE_QUESTIONNAIRE, DESC_QUESTIONNAIRE,
            DAT_CR_QUESTIONNAIRE, HASH_QUESTIONNAIRE, STATE_QUESTIONNAIRE, CAT_QUESTIONNAIRE, QUESTIONS};

    /**
     * create Questionnaire table
     *
     * @param database
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_QUESTIONNAIRE_TABLE);
    }

    /**
     * upgrade the Questionnaire table
     *
     * @param database
     * @param oldVersion
     * @param newVersion
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        // TODO
        database.execSQL("DROP TABLE IF EXISTS " + QUESTIONNAIRE_TABLE);
        onCreate(database);
    }
}
