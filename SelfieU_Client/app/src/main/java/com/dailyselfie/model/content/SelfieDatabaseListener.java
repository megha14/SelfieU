package com.dailyselfie.model.content;

import android.net.Uri;

import com.dailyselfie.model.mediator.webdata.SelfieRecord;

public interface SelfieDatabaseListener {

    void onQueryComplete(SelfieRecord selfie);

    void onInsertComplete(Uri uri);

    void onUpdateComplete(int result);

    void onDeleteComplete(int result);

}
