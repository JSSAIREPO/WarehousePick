package com.jssai.warehousepick.Util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.jssai.warehousepick.AndroidBmpUtil;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PictureService extends IntentService {

    public PictureService() {
        super("PictureService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void process(Context context, String uri, Handler handler) {
        Intent intent = new Intent(context, PictureService.class);
        intent.setAction("Process");
        intent.putExtra("imageUri", uri.toString());
        Messenger messenger = new Messenger(handler);
        intent.putExtra("handler", messenger);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals("Process")) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
                Uri imageUri = Uri.parse(intent.getStringExtra("imageUri"));
                Messenger messenger = (Messenger) intent.getExtras().get("handler");
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bArray = new byte[0];
                try {
                    bArray = bmpUtil.save(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String base64 = Base64.encodeToString(bos.toByteArray(), 0, bos.toByteArray().length, 0);
                Bundle bundle = new Bundle();
                bundle.putString("Message", base64);
                Message message = Message.obtain();
                message.setData(bundle);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
