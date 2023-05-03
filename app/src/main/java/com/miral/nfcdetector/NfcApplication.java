package com.miral.nfcdetector;

import android.app.Application;
import android.content.Context;

import com.miral.nfcdetector.constants.AppConstants;
import com.miral.nfcdetector.helper.DatabaseMigrationStrategyHelper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class NfcApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initRealmDatabase(this);
    }

    private void initRealmDatabase(Context context) {
        byte[] encryptionKey = Arrays.copyOf(AppConstants.APP_TAG.getBytes(StandardCharsets.UTF_8),
                AppConstants.REALM_AES_KEY_LENGTH);
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(AppConstants.DATABASE_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                //.encryptionKey(encryptionKey)
                .migration(new DatabaseMigrationStrategyHelper())
                .build();
        if (config != null) {
            Realm.setDefaultConfiguration(config);
        }
    }

}
