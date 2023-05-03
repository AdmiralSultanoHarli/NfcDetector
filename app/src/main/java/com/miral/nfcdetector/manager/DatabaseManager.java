package com.miral.nfcdetector.manager;

import android.content.Context;
import android.util.Log;

import com.miral.nfcdetector.constants.AppConstants;
import com.miral.nfcdetector.features.nfc.model.NfcData;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;

public class DatabaseManager {

    private static String TAG = DatabaseManager.class.getSimpleName();

    private static DatabaseManager mDatabaseManager;

    public DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager();
        }
        return mDatabaseManager;
    }

    public void insertOrUpdateNfcData(final NfcData nfcData) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(nfcData);
        } finally {
            if (realm != null) {
                realm.commitTransaction();
                realm.close();
            }
        }
    }

    public NfcData fetchNfcData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            final NfcData nfcData = realm.where(NfcData.class).findAll().last();
            return copyFromRealm(realm, nfcData);
        } catch (Exception e) {
            Log.e("", "" + e.getMessage());
        } finally {
            if (realm != null) {
                realm.commitTransaction();
                realm.close();
            }
        }
        return null;
    }

    public ArrayList<NfcData> getNfcData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            final ArrayList<NfcData> results = new ArrayList<>(realm.where(NfcData.class).findAll());
            return results;
        } finally {
            if (realm != null) {
                realm.commitTransaction();
                //realm.close();
            }
        }
    }

    public void deleteNfcData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            NfcData nfcData = realm.where(NfcData.class).findFirst();
            if (nfcData != null) {
                nfcData.deleteFromRealm();
            }

        } finally {
            if (realm != null) {
                realm.commitTransaction();
                realm.close();
            }
        }
    }

    public void clearNfcData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.delete(NfcData.class);
        } catch (Exception e) {
            Log.e("", "" + e.getMessage());
        } finally {
            if (realm != null) {
                realm.commitTransaction();
                realm.close();
            }
        }
    }

    public boolean isRealmCorrupted() {
        Realm realm = null;
        boolean isCorrupted = false;
        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalArgumentException e) {
            Log.e("RealmDB", "Realm DB is corrupted. Deleting.");
            isCorrupted = true;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return isCorrupted;
    }

    public void deleteRealmDB(Context context) {
        File realmFile = new File(context.getFilesDir(), AppConstants.DATABASE_NAME);
        if (realmFile != null) {
            deleteRecursive(realmFile.getParentFile());
        }
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    public void clearUserData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.delete(NfcData.class);
        } catch (Exception e) {
            Log.e("", "" + e.getMessage());
        } finally {
            if (realm != null) {
                realm.commitTransaction();
                realm.close();
            }
        }
    }

    public <T extends RealmObject> T copyFromRealm(Realm realm, T object) {
        if (object != null) {
            return realm.copyFromRealm(object);
        } else {
            return null;
        }
    }

}
