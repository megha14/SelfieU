package com.dailyselfie.model.content;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.dailyselfie.model.mediator.webdata.SelfieRecord;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncSelfieHandler extends AsyncQueryHandler {

    private static final AtomicInteger TOKEN = new AtomicInteger(0);

    private final SelfieDatabaseAdapter mAdapter;

    public AsyncSelfieHandler(ContentResolver cr) {
        this(cr, null);
    }

    public AsyncSelfieHandler(ContentResolver cr, SelfieDatabaseAdapter adapter) {
        super(cr);
        this.mAdapter = adapter;
    }

    public void insert(SelfieRecord selfie) {
        startInsert(TOKEN.incrementAndGet(),
                null,
                SelfieContract.SelfieTable.CONTENT_URI,
                SelfieContract.SelfieTable.getValues(selfie));
    }

    public void insert(List<SelfieRecord> selfies) {
        for (SelfieRecord selfie : selfies) {
            insert(selfie);
        }
    }

    public void query(long id) {
        startQuery(TOKEN.incrementAndGet(),
                null,
                SelfieContract.SelfieTable.CONTENT_URI,
                SelfieContract.SelfieTable.PROJECTION_ALL,
                SelfieContract.SelfieTable._ID + " = ?",
                new String[]{String.valueOf(id)},
                null);
    }

//    public void query(String filePath) {
//        startQuery(TOKEN.incrementAndGet(),
//                null,
//                SelfieContract.SelfieTable.CONTENT_URI,
//                SelfieContract.SelfieTable.PROJECTION_ALL,
//                SelfieContract.SelfieTable.FILE_PATH + " = ?",
//                new String[]{filePath},
//                null);
//    }

    public void query(String name) {
        startQuery(TOKEN.incrementAndGet(),
                null,
                SelfieContract.SelfieTable.CONTENT_URI,
                SelfieContract.SelfieTable.PROJECTION_ALL,
                SelfieContract.SelfieTable.TITLE + " = ?",
                new String[]{name},
                null);

    }

    public void queryAll() {
        startQuery(TOKEN.incrementAndGet(),
                null,
                SelfieContract.SelfieTable.CONTENT_URI,
                SelfieContract.SelfieTable.PROJECTION_ALL,
                null,
                null,
                null);
    }

    public void delete(SelfieRecord selfie) {
        startDelete(TOKEN.incrementAndGet(),
                null,
                SelfieContract.SelfieTable.buildUri(selfie),
                null,
                null);
    }

    public void update(SelfieRecord selfie) {
        startUpdate(TOKEN.incrementAndGet(),
                null,
                SelfieContract.SelfieTable.buildUri(selfie),
                SelfieContract.SelfieTable.getValues(selfie),
                null,
                null);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (mAdapter == null) return;
        SelfieRecord selfie = SelfieContract.SelfieTable.getSelfie(cursor);
        if (selfie != null) {
            mAdapter.onQueryComplete(selfie);
        } else {
            throw new SQLException("Query did not match a selfie.");
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        if (mAdapter == null) return;
        mAdapter.onInsertComplete(uri);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        if (mAdapter == null) return;
        mAdapter.onUpdateComplete(result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        if (mAdapter == null) return;
        mAdapter.onDeleteComplete(result);
    }

}
