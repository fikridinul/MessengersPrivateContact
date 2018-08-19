package com.thesisproject.fikri.messengers.messages;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeadlessSmsSendService extends Service {
    public HeadlessSmsSendService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //to do:Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
