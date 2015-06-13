package com.disycs.quizmo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class QuizmooSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static QuizmooSyncAdapter sQuizmooSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("QuizmooSyncService", "onCreate - QuizmooSyncService");
        synchronized (sSyncAdapterLock) {
            if (sQuizmooSyncAdapter == null) {
                sQuizmooSyncAdapter = new QuizmooSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sQuizmooSyncAdapter.getSyncAdapterBinder();
    }
}