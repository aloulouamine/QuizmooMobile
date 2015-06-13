package com.disycs.quizmo.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.disycs.quizmo.database.tables.QuestionnaireTable;
import com.disycs.quizmo.database.tables.UserTable;



public class QuizmooContentProvider extends ContentProvider {

    private static final String LOG_TAG = QuizmooContentProvider.class.getSimpleName();
    // database
    private QuizmooDataBaseHelper mOpenHelper;

    public static final String AUTHORITY = "com.disycs";

    public static final Uri USER_CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/" + UserTable.CONTENT_PATH);

    public static final Uri QUESTIONNAIRE_CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/" + QuestionnaireTable.CONTENT_PATH);

    private static final int USERS_LIST = 10;
    private static final int USER_ID = 11;
    private static final int QUESTIONNAIRES_LIST = 20;
    private static final int QUESTIONNAIRE_ID = 21;

    private static UriMatcher MASTERMIND_URI_MATCHER;

    static {
        MASTERMIND_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        MASTERMIND_URI_MATCHER.addURI(AUTHORITY,
                UserTable.CONTENT_PATH, USERS_LIST);

        MASTERMIND_URI_MATCHER.addURI(AUTHORITY,
                UserTable.CONTENT_PATH + "/#", USER_ID);

        MASTERMIND_URI_MATCHER.addURI(AUTHORITY,
                QuestionnaireTable.CONTENT_PATH, QUESTIONNAIRES_LIST);

        MASTERMIND_URI_MATCHER.addURI(AUTHORITY,
                QuestionnaireTable.CONTENT_PATH + "/#", QUESTIONNAIRE_ID);
    }



    public QuizmooContentProvider() {


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        Uri itemUri = null;
        int id = 0;

        switch (MASTERMIND_URI_MATCHER.match(uri)) {
            case QUESTIONNAIRES_LIST:
                id= database.delete(QuestionnaireTable.QUESTIONNAIRE_TABLE,null,null);
        }
        return id;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        Uri itemUri = null;
        long id = 0;

        switch (MASTERMIND_URI_MATCHER.match(uri)){
            case QUESTIONNAIRES_LIST:
                id = database.insert(QuestionnaireTable.QUESTIONNAIRE_TABLE, null, values);
                if(id>0){
                    itemUri = ContentUris.withAppendedId(uri,id);
                    getContext().getContentResolver().notifyChange(itemUri,null);
                }else{
                    throw new SQLException("Problem with inserting into "+
                        QuestionnaireTable.QUESTIONNAIRE_TABLE+" , uri :"+uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);

        }


        return itemUri;

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new QuizmooDataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {


        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (MASTERMIND_URI_MATCHER.match(uri)) {
            case USERS_LIST:
                queryBuilder.setTables(UserTable.USER_TABLE);
                break;
            case QUESTIONNAIRES_LIST :
                queryBuilder.setTables(QuestionnaireTable.QUESTIONNAIRE_TABLE);
                break;
            default:throw new IllegalArgumentException("Unsupported URI ;"+uri);

        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");

    }


}
