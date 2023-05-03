package com.miral.nfcdetector.helper;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * <p> This class is called when we have to update the Database.</p>
 * <p> Add new column into database by comparing new version with old version.</p>
 */
public class DatabaseMigrationStrategyHelper implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        //Update column when database is upgraded
        if (newVersion > oldVersion) {
            //add new columns here in-case required
        }
    }
}
