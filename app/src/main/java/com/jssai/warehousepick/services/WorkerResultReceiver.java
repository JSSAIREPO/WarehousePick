package com.jssai.warehousepick.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class WorkerResultReceiver extends ResultReceiver {
    private static final String TAG = "WorkerResultReceiver";
    private Receiver mReceiver;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * handler if given, or from an arbitrary thread if null.
     *
     * @param handler the handler object
     */
    public WorkerResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }


    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
