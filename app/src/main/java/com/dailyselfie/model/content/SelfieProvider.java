package com.dailyselfie.model.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.dailyselfie.model.content.SelfieContract.SelfieTable;

public class SelfieProvider extends ContentProvider {

    // helper constants for use with the UriMatcher
    private static final int SELFIE_LIST = 1;
    private static final int SELFIE_ID = 2;
    private static final UriMatcher URI_MATCHER;

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(SelfieContract.AUTHORITY, SelfieTable.TABLE_NAME, SELFIE_LIST);
        URI_MATCHER.addURI(SelfieContract.AUTHORITY, SelfieTable.TABLE_NAME + "/#", SELFIE_ID);
    }

    /**
     * Defines a handle to the database helper object.
     */
    private SelfieDatabaseHelper mOpenHelper;

    public SelfieProvider() {
        super();
    }

    /**
     * Return true if successfully started.
     *
     * @return true if successfully started
     */
    @Override
    public boolean onCreate() {
        // initialize the helper object
        this.mOpenHelper = new SelfieDatabaseHelper(getContext());
        return true;
    }

    /**
     * Method called to handle query requests from client applications.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(SelfieContract.SelfieTable.TABLE_NAME);

        switch (URI_MATCHER.match(uri)) {
            case SELFIE_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = SelfieContract.SelfieTable.DEFAULT_SORT_ORDER;
                }
                break;
            case SELFIE_ID:
                // limit query to one row at most:
                builder.appendWhere(appendId(null, uri));
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        // if we want to be notified of any changes:
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case SELFIE_LIST:
                return SelfieContract.SelfieTable.CONTENT_DIR_TYPE;
            case SELFIE_ID:
                return SelfieContract.SelfieTable.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    /**
     * Method called to handle insert requests from client applications.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != SELFIE_LIST) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        long id = -1;
        // values are only inserted if the row does not already exist in the database
        // insertWithOnConflict with SQLiteDatabase.CONFLICT_IGNORE is bugged
        // please see https://code.google.com/p/android/issues/detail?id=13045
        if (exists(values)) {
            id = values.getAsLong(SelfieTable._ID);
        } else {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            id = db.insert(SelfieContract.SelfieTable.TABLE_NAME, null, values);
        }

        if (id > 0) {
            Uri selfieUri = ContentUris.withAppendedId(uri, id);
            // notify all listeners of changes
            getContext().getContentResolver().notifyChange(selfieUri, null);
            return selfieUri;
        }

        Log.d(getClass().getSimpleName(), "id = " + id);
        for (String key : values.keySet()) {
            Log.d(getClass().getSimpleName(), key + " = " + values.get(key));
        }
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    /**
     * Check if the values already exist in the database
     *
     * @param values selfie values including the ID
     * @return <code>true</code> if the ID exists in the database,
     * <code>false</code> otherwise
     */
    private boolean exists(ContentValues values) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(SelfieTable._ID);
        sql.append(" FROM ");
        sql.append(SelfieTable.TABLE_NAME);
        sql.append(" WHERE ");
        sql.append(SelfieTable._ID);
        sql.append(" = ?");

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteStatement stmt = db.compileStatement(sql.toString());
        stmt.bindLong(1, values.getAsLong(SelfieTable._ID));

        try {
            stmt.simpleQueryForLong();
        } catch (SQLiteDoneException ex) {
            return false;
        } finally {
            stmt.close();
        }
        return true;
    }

    /**
     * Method called to handle delete requests from client applications.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int delCount = 0;

        switch (URI_MATCHER.match(uri)) {
            case SELFIE_LIST:
                delCount = db.delete(SelfieContract.SelfieTable.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case SELFIE_ID:
                delCount = db.delete(SelfieContract.SelfieTable.TABLE_NAME,
                        appendId(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        // notify all listeners of changes
        if (delCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return delCount;
    }

    /**
     * Method called to handle update requests from client applications.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updateCount = 0;

        switch (URI_MATCHER.match(uri)) {
            case SELFIE_LIST:
                updateCount = db.update(SelfieContract.SelfieTable.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case SELFIE_ID:
                updateCount = db.update(SelfieContract.SelfieTable.TABLE_NAME,
                        values, appendId(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        // notify all listeners of changes
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;
    }

    /**
     * Append an ID clause to the current where selection
     *
     * @param where The current selection, can be null.
     * @param uri   A URI having the ID value.
     * @return a new selection containing an ID clause
     */
    private String appendId(String where, Uri uri) {
        StringBuilder builder = new StringBuilder();
        builder.append(SelfieContract.SelfieColumns._ID)
                .append(" = ")
                .append(uri.getLastPathSegment());
        if (!TextUtils.isEmpty(where)) {
            builder.append(" AND ").append(where);
        }
        return builder.toString();
    }

}
