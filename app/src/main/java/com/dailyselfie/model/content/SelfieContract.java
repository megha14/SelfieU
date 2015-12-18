package com.dailyselfie.model.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.dailyselfie.model.mediator.webdata.SelfieRecord;

public final class SelfieContract {

    /**
     * The authority of the selfie provider.
     */
    public static final String AUTHORITY =
            "com.dailyselfie.model.content";
    /**
     * The model.model.content URI for the top-level selfie authority.
     */
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    /**
     * Columns for the selfie table
     */
    public interface SelfieColumns extends BaseColumns {
        String TITLE = "title";
        String CONTENT_TYPE = "content_type";
        String FILE_PATH = "file_path";
        String USER = "user";
        String DATA_URL = "data_url";
        String DATE_CREATED = "date_created";
        String DATE_MODIFIED = "date_modified";
    }

    /**
     * Inner class that defines the table contents of the selfie table.
     */
    public static final class SelfieTable implements SelfieColumns {

        /**
         * Selfies table name
         */
        public static final String TABLE_NAME = "selfie";

        /**
         * The model.model.content URI for this table.
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(SelfieContract.CONTENT_URI, TABLE_NAME);

        /**
         * The mime type of a list of selfies.
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;

        /**
         * The mime type of a single selfie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;

        /**
         * A projection of all columns in the table.
         */
        public static final String[] PROJECTION_ALL =
                {_ID, TITLE, CONTENT_TYPE, FILE_PATH, USER, DATA_URL, DATE_CREATED, DATE_MODIFIED};

        /**
         * The default sort order for queries.
         */
        public static final String DEFAULT_SORT_ORDER = _ID + " DESC";

        /**
         * Get the model.model.content values from a given selfie and a file path.
         *
         * @param selfie a selfie
         * @return the model.model.content values from a given selfie and a file path.
         */
        public static final ContentValues getValues(SelfieRecord selfie) {
            final ContentValues cv = new ContentValues();
            cv.put(_ID, selfie.getId());
            cv.put(TITLE, selfie.getName());
            cv.put(CONTENT_TYPE, selfie.getContentType());
            cv.put(FILE_PATH, selfie.getPath());
            cv.put(USER, selfie.getUser());
            cv.put(DATA_URL, selfie.getDataUrl());
            cv.put(DATE_CREATED,selfie.getDate_created());
            cv.put(DATE_MODIFIED,selfie.getDate_modified());
            return cv;
        }

        /**
         * Get the selfie from a given database cursor. The cursor must match one
         * only selfie row, otherwise <code>null</code> is returned.
         *
         * @param cursor a database cursor
         * @return the selfie initialized with the values of the cursor
         */
        public static final SelfieRecord getSelfie(Cursor cursor) {
            if (cursor != null && cursor.getCount() == 1) {
                final SelfieRecord selfie = new SelfieRecord();
                cursor.moveToFirst();
                selfie.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
                selfie.setName(cursor.getString(cursor.getColumnIndex(TITLE)));
                selfie.setContentType(cursor.getString(cursor.getColumnIndex(CONTENT_TYPE)));
                selfie.setPath(cursor.getString(cursor.getColumnIndex(FILE_PATH)));
                selfie.setUser(cursor.getString(cursor.getColumnIndex(USER)));
                selfie.setDataUrl(cursor.getString(cursor.getColumnIndex(DATA_URL)));
                selfie.setDate_created(cursor.getString(cursor.getColumnIndex(DATE_CREATED)));
                selfie.setDate_modified(cursor.getString(cursor.getColumnIndex(DATE_MODIFIED)));
                cursor.close();
                return selfie;
            }
            return null;
        }

        /**
         * Return a Uri that points to the row containing a given id.
         *
         * @param selfie
         * @return Uri
         */
        public static Uri buildUri(SelfieRecord selfie) {
            return ContentUris.withAppendedId(CONTENT_URI, selfie.getId());
        }

        /**
         * Converts the last path segment to a long.
         *
         * @param uri a URI
         * @return the long conversion of the last segment or -1 if the path does not match a selfie URI
         */
        public static long parseId(Uri uri) {
            try {
                return ContentUris.parseId(uri);
            } catch (UnsupportedOperationException | NumberFormatException e) {
                return -1;
            }
        }

    }

}
