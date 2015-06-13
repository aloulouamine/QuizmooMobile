package com.disycs.quizmo.database;

/**
 * Created by amine on 13/06/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.disycs.quizmo.database.tables.QuestionnaireTable;
import com.disycs.quizmo.database.tables.UserTable;


public class QuizmooDataBaseHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "QuizmooDB";
    // TABLE USER
    private final String USER_TABLE_NAME = "User";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_PASSWORD = "password";
    //TABLE QUESTIONNAIRE
    private final String QUESTIONNAIRE_TABLE_NAME = "Questionnaire";
    private final String KEY_ID_QUESTIONNAIRE = "idQuest";
    private final String KEY_TITLE_QUESTIONNAIRE = "titQuest";
    private final String KEY_DESC_QUESTIONNAIRE = "descQuest";
    private final String KEY_DAT_CR_QUESTIONNAIRE = "datCrQuest";
    private final String KEY_HASH_QUESTIONNAIRE = "hashQuest";
    private final String KEY_STATE_QUESTIONNAIRE = "stateQuest";
    private final String KEY_CAT_QUESTIONNAIRE = "categQuest";
    private final String KEY_QUESTIONS = "Questions";
    private final String USER_COLUMNS[] = {KEY_USER_NAME, KEY_PASSWORD};



    public QuizmooDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        UserTable.onCreate(db);
        QuestionnaireTable.onCreate(db);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        UserTable.onUpgrade(db, oldVersion, newVersion);
        QuestionnaireTable.onUpgrade(db, oldVersion, newVersion);
    }



}