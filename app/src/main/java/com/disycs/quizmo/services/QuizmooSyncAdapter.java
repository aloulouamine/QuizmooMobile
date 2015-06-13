package com.disycs.quizmo.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.disycs.quizmo.QuestionnairesActivity;
import com.disycs.quizmo.R;
import com.disycs.quizmo.api.HttpProxy;
import com.disycs.quizmo.database.QuizmooContentProvider;
import com.disycs.quizmo.database.tables.QuestionnaireTable;
import com.disycs.quizmo.model.Questionnaire;
import com.disycs.quizmo.model.Token;
import com.disycs.quizmo.model.questions.Question;

import java.util.ArrayList;
import java.util.Vector;


public class QuizmooSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = QuizmooSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the API, in milliseconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private AccountManager mAccountManager;

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


        String  token  = mAccountManager.peekAuthToken(account,authority);

        Log.v(LOG_TAG,"account name sync : "+account.name);
        Log.v(LOG_TAG,"token : "+token);
        //Since the API does not support sending only new entries we have to request them then compare hash for each questionnaire
        getContext().getContentResolver().delete(QuizmooContentProvider.QUESTIONNAIRE_CONTENT_URI,null,null);


        for(Questionnaire.STATE state : Questionnaire.STATE.values()){

            ArrayList<Questionnaire> questionnaireList = HttpProxy.getQuestionnaires(Token.getToken(), state);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(questionnaireList.size());
            for (Questionnaire questionnaire : questionnaireList) {
                ContentValues questionnaireContentValues = new ContentValues();
                questionnaireContentValues.put(QuestionnaireTable.ID_QUESTIONNAIRE,questionnaire.getId());
                questionnaireContentValues.put(QuestionnaireTable.TITLE_QUESTIONNAIRE,questionnaire.getTitle());
                questionnaireContentValues.put(QuestionnaireTable.DESC_QUESTIONNAIRE,questionnaire.getDescription());
                questionnaireContentValues.put(QuestionnaireTable.DAT_CR_QUESTIONNAIRE,questionnaire.getDateOfCreation().toString());
                questionnaireContentValues.put(QuestionnaireTable.HASH_QUESTIONNAIRE,questionnaire.getHash());
                questionnaireContentValues.put(QuestionnaireTable.STATE_QUESTIONNAIRE,questionnaire.getState().getCode());
                questionnaireContentValues.put(QuestionnaireTable.CAT_QUESTIONNAIRE,questionnaire.getCategory().getCode());
                questionnaireContentValues.put(QuestionnaireTable.QUESTIONS,questionnaire.getThumbnail());
                cVVector.add(questionnaireContentValues);
            }
            if(cVVector.size()>0){
                ContentValues[] cVArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cVArray);
                getContext().getContentResolver().bulkInsert(QuizmooContentProvider.QUESTIONNAIRE_CONTENT_URI, cVArray);
                getContext().getContentResolver().notifyChange(QuizmooContentProvider.QUESTIONNAIRE_CONTENT_URI,null);
            }

        }


    }



    public QuizmooSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);


    }




    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.sync_account_type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle params = new Bundle();
            params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
            params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
            params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).setExtras(params).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                "com.disycs", bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                "Quizmoo", context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        QuizmooSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}
