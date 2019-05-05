package com.jssai.warehousepick;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintDocumentAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.jssai.warehousepick.Model.WS_eSignOrder;
import com.jssai.warehousepick.Util.Constants;
import com.jssai.warehousepick.Util.NetworkService;
import com.jssai.warehousepick.Model.WS_PostSalesOrder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    PrintDocumentAdapter pda;
    @BindView(R.id.signature_pad)
    SignaturePad signaturePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    public void getData(View view) {
        String url = "http://76.174.207.208:7048/DynamicsNAV90/OData/Company('YGD')/e_SignOrder(4)?$format=json";
        StringBuilder request = new StringBuilder();
        try {
            request = new StringBuilder("http://76.174.207.208:7048/DynamicsNAV90/OData/Company").append(URLEncoder.encode("('YGD')", "UTF-8")).append("/e_SignOrder").append(URLEncoder.encode("(1)", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(Constants.TAG, " " + request.toString());
        Gson gson = new Gson();
        WS_eSignOrder eSignOrder = new WS_eSignOrder();
        eSignOrder.setNo("23");
        eSignOrder.setMobile_Comment("!!! Updated with Android   !!!!!!");

        //  getPicture();

        String jsonString = gson.toJson(eSignOrder).toString();
        // NetworkService.SOAP(url,this,new Handler());
//        Map<String,String> map = new HashMap<>();
//        map.put(WS_eSignOrder.Entry_No_string,"1111");
//
//        com.jssai.warehousepick.XSDobjects.WS_eSignOrder WSeSignOrder = new com.jssai.warehousepick.XSDobjects.WS_eSignOrder();
//        WSeSignOrder.setProperty(3,"Test SOPA");

        //  NetworkService.SOAP(NavionUrl.ESIGNATURE.METHOD_CREATE,this,new Handler(),WS_eSignOrder.class,WSeSignOrder,null);
        //NetworkService.SOAP(NavionUrl.ESIGNATURE.METHOD_Read,this,new Handler(),WS_eSignOrder.class,null,map);
        // NetworkService.SOAPTEST(NavionUrl.ESIGNATURE_URL,this,new Handler());
        //  NetworkService.SOAP(NavionUrl.ESIGNATURE.METHOD_READ_MULTIPLE,this,new Handler(),WS_eSignOrder.class,null,null);

//        String etag = "16;UcMAAACHxQAAAAAA6;8723640;";
//        String Etag ="";
//       // "W/\"'16%3BUcMAAACHxQAAAAAA6%3B8720440%3B'\"";
//        try {
//            String encoed = URLEncoder.encode(etag,"UTF-8");
//             Etag = "W/\"'"+encoed+"'\"";
//            Log.d("Etag",Etag);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Map<String, String> map = new HashMap<>();
//        map.put(WS_eSignOrder.Entry_No_string, "36249");
//        NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new GetRecordHandler(new WeakReference<MainActivity>(this)), WS_eSignOrder.class, null, map);
//        //  NetworkService.Put("http://76.174.207.208:7048/DynamicsNAV90/OData/Company('YGD')/WS_eSignOrder(197)?$format=json", gson.toJson(eSignOrder).toString(),Etag,new Handler(),this);
          NetworkService.SOAPTEST("",this,new Handler());



//        pda = new PrintDocumentAdapter() {
//            @Override
//            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
//                InputStream input = null;
//                OutputStream output = null;
//
//                try {
//                    //      File encryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"encrypted.mp4");
//                    // File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"pdf.pdf");
//                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                    String filename = "test.pdf";
//                    File file = new File(dir, filename);
//                    if (file.exists()) {
//                        input = new FileInputStream(file);
//                        output = new FileOutputStream(destination.getFileDescriptor());
//
//                        byte[] buf = new byte[1024];
//                        int bytesRead;
//
//                        while ((bytesRead = input.read(buf)) > 0) {
//                            output.write(buf, 0, bytesRead);
//                        }
//                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//                    }
//
//                } catch (FileNotFoundException ee) {
//                    //Catch exception
//                } catch (Exception e) {
//                    //Catch exception
//                }
//
//            }
//
//            @Override
//            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//                if (cancellationSignal.isCanceled()) {
//                    callback.onLayoutCancelled();
//                    return;
//                }
//                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
//
//                callback.onLayoutFinished(pdi, true);
//            }
//        };
//        Bitmap bitmap = signaturePad.getSignatureBitmap();
//        bitmap =bitmap.createScaledBitmap(bitmap,100,100,false);
////        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
////        bmOptions.inJustDecodeBounds = true;
////        int scaleFactor = 1;
////            scaleFactor = Math.min(10, 10);
////        bmOptions.inJustDecodeBounds = false;
////        bmOptions.inSampleSize = scaleFactor;
////        Bitmap resize = BitmapFactory.de
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File file = new File(dir, "sign.jpg");
//        File outFile = new File(dir, "Output.bmp");
        String sdcardBmpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sample.bmp";
        //Bitmap testBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_text);
       // Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.placeholder);
//        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
//        boolean isSaveResult = false;
//        try {
//            isSaveResult = bmpUtil.save(bitmap, outFile.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (isSaveResult) {
//            Log.d(Constants.TAG, "Succcess");
//        }

    }

//    static class GetRecordHandler extends Handler {
//        WeakReference<MainActivity> salesOrderActivityWeakReference;
//
//        public GetRecordHandler(WeakReference<MainActivity> salesOrderActivityWeakReference) {
//            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            MainActivity salesOrderActivity = salesOrderActivityWeakReference.get();
//            if (salesOrderActivity != null) {
//                int args = msg.arg1;
//                switch (args) {
//                    case 1:
//                        Bundle bundle = msg.getData();
//                        // WS_eSignOrder ws_eSignOrder = (WS_eSignOrder) bundle.getSerializable(Constants.MESSAGE);
//                        //  Log.d(Constants.TAG,ws_eSignOrder.getKey());
//                        break;
//                    case 0:
//                        break;
//                }
//            }
//        }
//    }

//    public void getPicture() {
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, "http://45.51.149.249/api/picture", null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//
//                Gson gson = new Gson();
//                Type type = new TypeToken<ArrayList<Picture>>() {
//                }.getType();
//                ArrayList<Picture> pictures = gson.fromJson(response.toString(), type);
//                if (pictures != null) {
//                    if (pictures.size() > 0) {
//                        Picture picture = pictures.get(0);
//                        byte[] z = Base64.decode(picture.Picture, Base64.DEFAULT);
//                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                        String filename = "test.pdf";
//                        File f = new File(dir, filename);
//                        try {
//                            f.createNewFile();
//                            OutputStream outStream = new FileOutputStream(f);
//                            outStream.write(z);
//                            outStream.flush();
//                            outStream.close();
//                            Toast.makeText(MainActivity.this, f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                            PrintManager printManager = (PrintManager) MainActivity.this.getSystemService(Context.PRINT_SERVICE);
//                            String jobName = MainActivity.this.getString(R.string.app_name) + " Document";
//                            printManager.print(jobName, pda, null);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        Log.d("Tag", response.toString());
//                    }
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("Tag", error.toString());
//            }
//        });
////        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://35.154.63.116//api/Merchant/action/GetShopPicture/1", null, new Response.Listener<JSONObject>() {
////            @Override
////            public void onResponse(JSONObject response) {
////                Gson gson = new Gson();
////                Type type = new TypeToken<Picture>(){}.getType();
////                try {
////                    String jsonObject = response.getString("data");
////                    Picture pictures = gson.fromJson(jsonObject.toString(),type);
////                    byte[] z = Base64.decode(pictures.Picture,Base64.DEFAULT);
////                    Bitmap bitmap = BitmapFactory.decodeByteArray(z,0,z.length);
////                    Log.d("Tag",response.toString());
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////
////            }
////        }, new Response.ErrorListener() {
////            @Override
////            public void onErrorResponse(VolleyError error) {
////
////            }
////        });
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonObjectRequest);
//    }
}


//    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
//        Context context;
//        PrintedPdfDocument mPdfDocument;
//        public MyPrintDocumentAdapter(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//            mPdfDocument = new PrintedPdfDocument(context, newAttributes);
//            // Respond to cancellation request
//            if (cancellationSignal.isCancelled() ) {
//                callback.onLayoutCancelled();
//                return;
//            }
//            // Compute the expected number of printed pages
//            int pages = computePageCount(newAttributes);
//
//            if (pages > 0) {
//                // Return print information to print framework
//                PrintDocumentInfo info = new PrintDocumentInfo
//                        .Builder("print_output.pdf")
//                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
//                        .setPageCount(pages);
//                .build();
//                // Content layout reflow is complete
//                callback.onLayoutFinished(info, true);
//            } else {
//                // Otherwise report an error to the print framework
//                callback.onLayoutFailed("Page count calculation failed.");
//            }
//        }
//        private int computePageCount(PrintAttributes printAttributes) {
//            int itemsPerPage = 4; // default item count for portrait mode
//
//            PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
//            if (!pageSize.isPortrait()) {
//                // Six items per page in landscape orientation
//                itemsPerPage = 6;
//            }
//
//            // Determine number of print items
//            int printItemCount = getPrintItemCount();
//
//            return (int) Math.ceil(printItemCount / itemsPerPage);
//        }
//
//
//        @Override
//        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
//// check if it's in the output range.
//            for (int i = 0; i < totalPages; i++) {
//                // Check to see if this page is in the output range.
//                if (containsPage(pageRanges, i)) {
//                    // If so, add it to writtenPagesArray. writtenPagesArray.size()
//                    // is used to compute the next output page index.
//                    writtenPagesArray.append(writtenPagesArray.size(), i);
//                    PdfDocument.Page page = mPdfDocument.startPage(i);
//
//                    // check for cancellation
//                    if (cancellationSignal.isCancelled()) {
//                        callback.onWriteCancelled();
//                        mPdfDocument.close();
//                        mPdfDocument = null;
//                        return;
//                    }
//
//                    // Draw page content for printing
//                    drawPage(page);
//
//                    // Rendering is complete, so page can be finalized.
//                    mPdfDocument.finishPage(page);
//                }
//        }
//    }
//}


