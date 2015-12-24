package com.dailyselfie.model.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * The database helper used by the SelfieProvider to create
 * and manage its underling database.
 */
public class SelfieDatabaseHelper extends SQLiteOpenHelper {

    /**
     * A string that defines the SQL statement for creating a table
     */
    private static final String SQL_CREATE =
            "CREATE TABLE " + SelfieContract.SelfieTable.TABLE_NAME + " ("
                    + SelfieContract.SelfieColumns._ID + " INTEGER PRIMARY KEY, "
                    + SelfieContract.SelfieColumns.TITLE + " TEXT, "
                    + SelfieContract.SelfieColumns.CONTENT_TYPE + " TEXT, "
                    + SelfieContract.SelfieColumns.FILE_PATH + " TEXT, "
                    + SelfieContract.SelfieColumns.USER+ " TEXT, "
                    + SelfieContract.SelfieColumns.DATA_URL+ " TEXT, "
                    + SelfieContract.SelfieColumns.DATE_CREATED + " TEXT, "
                    + SelfieContract.SelfieColumns.DATE_MODIFIED + " TEXT"
                    + " )";
    /**
     * Database name.
     */
    private static String DATABASE_NAME = "selfie_db";
    /**
     * Database version number, which is updated with each schema change.
     */
    private static int DATABASE_VERSION = 1;

    /**
     * Constructor - initialize database name and version, but don't actually
     * construct the database (which is done in the onCreate() hook method). It
     * places the database in the application's cache directory, which will be
     * automatically cleaned up by Android if the device runs low on storage
     * space.
     *
     * @param context
     */
    public SelfieDatabaseHelper(Context context) {
        super(context,
                context.getCacheDir() + File.separator + DATABASE_NAME,
                null,
                DATABASE_VERSION);
    }

    /**
     * Hook method called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table.
        db.execSQL(SQL_CREATE);
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete the existing tables.
        db.execSQL("DROP TABLE IF EXISTS " + SelfieContract.SelfieTable.TABLE_NAME);
        // Create the new tables.
        onCreate(db);
    }

}
