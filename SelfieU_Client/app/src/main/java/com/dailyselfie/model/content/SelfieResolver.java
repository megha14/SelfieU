package com.dailyselfie.model.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.dailyselfie.model.content.SelfieContract.SelfieTable;
import com.dailyselfie.model.mediator.webdata.SelfieRecord;

import java.util.ArrayList;
import java.util.List;


public class SelfieResolver {

    private static final String LOG_TAG = SelfieResolver.class.getSimpleName();

    private final ContentResolver mContentResolver;

    public SelfieResolver(ContentResolver cr) {
        this.mContentResolver = cr;
    }

    public Uri insert(SelfieRecord selfie) {
        return mContentResolver.insert(
                SelfieTable.CONTENT_URI,
                SelfieTable.getValues(selfie));
    }

    public int insert(List<SelfieRecord> selfies) {
        ContentValues[] cvs = new ContentValues[selfies.size()];
        for (int i = 0; i < cvs.length; i++) {
            cvs[i] = SelfieTable.getValues(selfies.get(i));
        }
        return mContentResolver.bulkInsert(
                SelfieTable.CONTENT_URI,
                cvs);
    }

    public SelfieRecord query(long id) {
        Cursor cursor = mContentResolver.query(
                SelfieTable.CONTENT_URI,
                SelfieTable.PROJECTION_ALL,
                SelfieTable._ID + " = ?",
                new String[]{String.valueOf(id)},
                null);
        SelfieRecord selfieRecord = SelfieTable.getSelfie(cursor);
        cursor.close();
        return selfieRecord;
    }

    /*public SelfieRecord query(String filePath) {
        Cursor cursor = mContentResolver.query(
                SelfieTable.CONTENT_URI,
                SelfieTable.PROJECTION_ALL,
                SelfieTable.FILE_PATH + " = ?",
                new String[]{filePath},
                null);
        return SelfieTable.getSelfie(cursor);
    }*/

    public SelfieRecord query(String name) {
        Cursor cursor = mContentResolver.query(
                SelfieTable.CONTENT_URI,
                SelfieTable.PROJECTION_ALL,
                SelfieTable.TITLE + " = ?",
                new String[]{name},
                null);
        return SelfieTable.getSelfie(cursor);
    }

    public List<SelfieRecord> queryAll() {
        List<SelfieRecord> selfieRecords = new ArrayList<SelfieRecord>();
        Cursor cursor = mContentResolver.query(
                SelfieTable.CONTENT_URI,
                SelfieTable.PROJECTION_ALL,
                null,
                null,
                null);

        Log.d(LOG_TAG, String.valueOf(cursor.getCount()));
        if (cursor!=null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                final SelfieRecord selfie = new SelfieRecord();
                selfie.setId(cursor.getLong(cursor.getColumnIndex(SelfieTable._ID)));
                selfie.setName(cursor.getString(cursor.getColumnIndex(SelfieTable.TITLE)));
                selfie.setContentType(cursor.getString(cursor.getColumnIndex(SelfieTable.CONTENT_TYPE)));
                selfie.setPath(cursor.getString(cursor.getColumnIndex(SelfieTable.FILE_PATH)));
                selfie.setUser(cursor.getString(cursor.getColumnIndex(SelfieTable.USER)));
                selfie.setDataUrl(cursor.getString(cursor.getColumnIndex(SelfieTable.DATA_URL)));
                selfie.setDate_created(cursor.getString(cursor.getColumnIndex(SelfieTable.DATE_CREATED)));
                selfie.setDate_modified(cursor.getString(cursor.getColumnIndex(SelfieTable.DATE_MODIFIED)));

                selfieRecords.add(selfie);
                Log.d(LOG_TAG, selfieRecords.toString());
            }
            cursor.close();
        }

        return selfieRecords;
    }

    public SelfieRecord queryLastInserted(){
        Cursor cursor = mContentResolver.query(
                SelfieTable.CONTENT_URI,
                SelfieTable.PROJECTION_ALL,
                null,
                null,
                null);

        cursor.moveToFirst();
        final SelfieRecord selfie = new SelfieRecord();
        selfie.setId(cursor.getLong(cursor.getColumnIndex(SelfieTable._ID)));
        selfie.setName(cursor.getString(cursor.getColumnIndex(SelfieTable.TITLE)));
        selfie.setContentType(cursor.getString(cursor.getColumnIndex(SelfieTable.CONTENT_TYPE)));
        selfie.setPath(cursor.getString(cursor.getColumnIndex(SelfieTable.FILE_PATH)));
        selfie.setUser(cursor.getString(cursor.getColumnIndex(SelfieTable.USER)));
        selfie.setDataUrl(cursor.getString(cursor.getColumnIndex(SelfieTable.DATA_URL)));
        selfie.setDate_created(cursor.getString(cursor.getColumnIndex(SelfieTable.DATE_CREATED)));
        selfie.setDate_modified(cursor.getString(cursor.getColumnIndex(SelfieTable.DATE_MODIFIED)));
        cursor.close();
        return selfie;
    }

    public List<SelfieRecord> queryAll(String User) {
        List<SelfieRecord> selfieRecords = new ArrayList<SelfieRecord>();
        Cursor cursor = mContentResolver.query(
                SelfieTable.CONTENT_URI,
                SelfieTable.PROJECTION_ALL,
                SelfieTable.USER + " = ?",
                new String[]{User},
                null);

        Log.d(LOG_TAG, String.valueOf(cursor.getCount()));
        if (cursor!=null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                final SelfieRecord selfie = new SelfieRecord();
                selfie.setId(cursor.getLong(cursor.getColumnIndex(SelfieTable._ID)));
                selfie.setName(cursor.getString(cursor.getColumnIndex(SelfieTable.TITLE)));
                selfie.setContentType(cursor.getString(cursor.getColumnIndex(SelfieTable.CONTENT_TYPE)));
                selfie.setPath(cursor.getString(cursor.getColumnIndex(SelfieTable.FILE_PATH)));
                selfie.setUser(cursor.getString(cursor.getColumnIndex(SelfieTable.USER)));
                selfie.setDataUrl(cursor.getString(cursor.getColumnIndex(SelfieTable.DATA_URL)));
                selfie.setDate_created(cursor.getString(cursor.getColumnIndex(SelfieTable.DATE_CREATED)));
                selfie.setDate_modified(cursor.getString(cursor.getColumnIndex(SelfieTable.DATE_MODIFIED)));

                selfieRecords.add(selfie);
                Log.d(LOG_TAG, selfieRecords.toString());
            }
            cursor.close();
        }

        return selfieRecords;
    }

    public int delete(SelfieRecord selfie) {
        return mContentResolver.delete(
                SelfieTable.buildUri(selfie),
                null,
                null);
    }

    public int deleteAll() {
        return mContentResolver.delete(
                SelfieTable.CONTENT_URI,
                null,
                null);
    }

    public int update(SelfieRecord selfie) {
        return mContentResolver.update(
                SelfieTable.buildUri(selfie),
                SelfieTable.getValues(selfie),
                null,
                null);
    }

}
