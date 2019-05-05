package com.jssai.warehousepick;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jssai.warehousepick.Fragments.PictureFragment;
import com.jssai.warehousepick.Fragments.SignFragment;
import com.jssai.warehousepick.Model.WS_PostSalesOrder;
import com.jssai.warehousepick.Model.WS_PostedeSignOrder;
import com.jssai.warehousepick.Model.WS_eSignOrder;
import com.jssai.warehousepick.Util.Constants;
import com.jssai.warehousepick.Util.GlobalMethods;
import com.jssai.warehousepick.Util.NavionUrl;
import com.jssai.warehousepick.Util.NetworkCheck;
import com.jssai.warehousepick.Util.NetworkService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// #1 Nov-Dec 2017 ,Download PDF and print using Google cloud print<To download PDF from url with authentication>

public class SalesOrderActivity extends AppCompatActivity implements SignFragment.OnFragmentInteractionListener, PictureFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = "Sales Order TAG";
    @BindView(R.id.saleOrderNo)
    TextView saleOrderNo;
    @BindView(R.id.customerno)
    TextView customerno;
    @BindView(R.id.no)
    TextView no;
    @BindView(R.id.shiptocode)
    TextView shiptocode;
    @BindView(R.id.shiptoname)
    TextView shiptoname;
    @BindView(R.id.shipmetdate)
    TextView shipmetdate;
    @BindView(R.id.customerPo)
    TextView customerPo;
    @BindView(R.id.shiptopo)
    TextView shiptopo;
    @BindView(R.id.mobilecomment)
    TextView mobilecomment;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    WS_eSignOrder WSeSignOrder;
    WS_PostSalesOrder ws_postSalesOrder;
    ProgressDialog progressDialog;
    String type;
    String uri;


    ImageView imageView1, imageView2, imageView3;
    @BindView(R.id.postButton)
    Button postButton;
    @BindView(R.id.printButton)
    Button printButton;
    @BindView(R.id.activity_sales_order2)
    RelativeLayout activitySalesOrder2;
    String signedNow;
    ViewPagerAdapter viewPagerAdapter;
    HashMap<String, String> params;
    ArrayList<String> tabs_array;
    static PrintManager printManager;
    static String jobName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);
        ButterKnife.bind(this);
        signedNow = "";
        WSeSignOrder = null;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data ....");
        progressDialog.setCancelable(false);

        String title = getResources().getString(R.string.app_name) + " - Sales Order";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        tabs_array = new ArrayList<>();
        tabs_array.add("Driver");
        tabs_array.add("Shipping clerk");
        tabs_array.add("Receiver");
        tabs_array.add("Picture1");
        tabs_array.add("Picture2");
        tabs_array.add("Picture3");
        printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        jobName = getString(R.string.app_name) + " Document";


        Intent intent = getIntent();
        if (intent != null) {
            WSeSignOrder = (WS_eSignOrder) intent.getSerializableExtra(Constants.MESSAGE);
            if (WSeSignOrder != null) {
                progressDialog.setMessage("Loading Data ....");
                progressDialog.show();
                Map<String, String> map = new HashMap<>();
                map.put(WS_eSignOrder.Entry_No_string, WSeSignOrder.Entry_No);
                NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new GetRecordHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
                saleOrderNo.setText(WSeSignOrder.getEntry_No());
                customerno.setText(WSeSignOrder.getSell_to_Customer_No());
                no.setText(WSeSignOrder.getNo());
                shiptocode.setText(WSeSignOrder.getShip_to_Code());
                shiptoname.setText(WSeSignOrder.getShip_to_Name());
                shipmetdate.setText(WSeSignOrder.getShipment_Date());
                customerPo.setText(WSeSignOrder.getCustomer_PO_No());
                shiptopo.setText(WSeSignOrder.getShip_To_PO_No());
                mobilecomment.setText(WSeSignOrder.getMobile_Comment());
                if (Boolean.parseBoolean(WSeSignOrder.Driver_Signature_Signed) && (Boolean.parseBoolean(WSeSignOrder.Shipping_Clerk_Signature_Sgd) && Boolean.parseBoolean(WSeSignOrder.Receiver_Signature_Signed))) {
                    postButton.setEnabled(true);
                    printButton.setEnabled(true);
                    postButton.setTextColor(Color.WHITE);
                    printButton.setTextColor(Color.WHITE);
                } else {
                    postButton.setEnabled(false);
                    postButton.setTextColor(Color.GRAY);
                    printButton.setTextColor(Color.GRAY);
                    printButton.setEnabled(false);
                }
                viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabs_array);
                viewpager.setAdapter(viewPagerAdapter);
                tabs.setupWithViewPager(viewpager);
            }

        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case 1:
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    imageView1.setImageBitmap(photo);
//                    break;
//                case 2:
//                    Bitmap photo1 = (Bitmap) data.getExtras().get("data");
//                    imageView2.setImageBitmap(photo1);
//                    break;
//                case 3:
//                    Bitmap photo2 = (Bitmap) data.getExtras().get("data");
//                    imageView3.setImageBitmap(photo2);
//                    break;
//            }
//        }
//    }

//    @OnClick(R.id.floatingActionButton)
//    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.floatingActionButton:
//                alertDialog.show();
//                imageView3.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(viewpager.getWindowToken(), 0);
//                break;
//        }
//
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewpager.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

//    public String convertImage(Bitmap bitmap) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//        byte[] bArray = new byte[0];
//        try {
//            bArray = bmpUtil.save(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Base64.encodeToString(bos.toByteArray(), 0, bos.toByteArray().length, 0);
////        int bytes = bitmap.getByteCount();
////        ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
////        bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
////        byte[] array = buffer.array();
////        String d = Base64.encodeToString(array,0,array.length,0);
//        //return Base64.encodeToString(bArray,0,bArray.length,0);
//
//
//    }
//    public void uploadPicture(String base64){
//        HashMap<String, String> map = new HashMap<>();
//        map.put(WS_eSignOrder.Entry_No_string, WSeSignOrder.Entry_No);
//        params = new HashMap<>();
//        switch (type) {
//            case "Picture1":
//                signedNow = "Picture1";
//                params.put("eSignRec", WSeSignOrder.Key);
//                params.put("driverSign", "");
//                params.put("shippingSign", "");
//                params.put("receiverSign", "");
//                params.put("picture1", base64);
//                params.put("picture2", "");
//                params.put("picture3", "");
//                params.put("comment", "");
//                NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdateSignatureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
//                break;
//            case "Picture2":
//                signedNow = "Picture2";
//                params.put("eSignRec",WSeSignOrder.Key);
//                params.put("driverSign", "");
//                params.put("shippingSign", "");
//                params.put("receiverSign", "");
//                params.put("picture1", "");
//                params.put("picture2", base64);
//                params.put("picture3", "");
//                params.put("comment", "");
//                NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdateSignatureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
//                break;
//            case "Picture3":
//                signedNow = "Picture3";
//                HashMap<String, String> param2 = new HashMap<>();
//                params.put("eSignRec", WSeSignOrder.Key);
//                params.put("driverSign", "");
//                params.put("shippingSign", "");
//                params.put("receiverSign", "");
//                params.put("picture1", "");
//                params.put("picture2", "");
//                params.put("picture3", base64);
//                params.put("comment", "");
//                NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdateSignatureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
//                break;
//        }
//
//    }
//
//    class PictureProcessHandler extends Handler {
//        private WeakReference<SalesOrderActivity> SalesOrderActivityWeakReference;
//
//        public PictureProcessHandler(SalesOrderActivity salesOrderActivity) {
//            this.SalesOrderActivityWeakReference = new WeakReference<>(salesOrderActivity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            SalesOrderActivity salesOrderActivity = SalesOrderActivityWeakReference.get();
//            if (salesOrderActivity != null) {
//                Bundle bundle = msg.getData();
//                String base64 = bundle.getString("Message");
//                salesOrderActivity.uploadPicture(base64);
//            }
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salsesorder_activity, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void Capture(SignaturePad signaturePad, String userType) {
        Bitmap bitmap = signaturePad.getSignatureBitmap();
        if (bitmap != null) {
            progressDialog.setMessage("uploading...");
            progressDialog.show();
            bitmap = bitmap.createScaledBitmap(bitmap, 300, 150, false);
            AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
            Map<String, String> map = new HashMap<>();
            map.put(WS_eSignOrder.Entry_No_string, WSeSignOrder.Entry_No);
            try {
                byte[] bytes = bmpUtil.save(bitmap);
                params = new HashMap<>();

                String base64String = Base64.encodeToString(bytes, 0, bytes.length, 0);
                switch (userType) {
                    case "Driver":
                        signedNow = "Driver";
                        params.put("eSignRec", WSeSignOrder.Key);
                        params.put("driverSign", base64String);
                        params.put("shippingSign", "");
                        params.put("receiverSign", "");
                        params.put("picture1", "");
                        params.put("picture2", "");
                        params.put("picture3", "");
                        params.put("comment", "Messgae from Android");
                        NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdateSignatureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
                        signaturePad.clear();
                        break;
                    case "Shipping clerk":
                        signedNow = "Shipping clerk";
                        params.put("eSignRec", WSeSignOrder.Key);
                        params.put("driverSign", "");
                        params.put("shippingSign", base64String);
                        params.put("receiverSign", "");
                        params.put("picture1", "");
                        params.put("picture2", "");
                        params.put("picture3", "");
                        params.put("comment", "Messgae from Android");
                        NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdateSignatureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
                        signaturePad.clear();
                        break;
                    case "Receiver":
                        signedNow = "Receiver";
                        params.put("eSignRec", WSeSignOrder.Key);
                        params.put("driverSign", "");
                        params.put("shippingSign", "");
                        params.put("receiverSign", base64String);
                        params.put("picture1", "");
                        params.put("picture2", "");
                        params.put("picture3", "");
                        params.put("comment", "Messgae from Android");
                        NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdateSignatureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
                        signaturePad.clear();
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "sign.png");
//            try {
//                FileOutputStream outputStream = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            //byte[] backToBytes = Base64.decodeBase64(base64String);


        } else {
            Toast.makeText(this, "Please input a Signature", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void Capture(ImageView imageView, String type, Uri uri) {
        this.type = type;
        this.signedNow = type;
        if (uri == null) {
            uri = uri.EMPTY;
        }
        this.uri = uri.toString();
        Object image = imageView.getDrawable();
        if (!(image instanceof VectorDrawable)) {
            progressDialog.setMessage("uploading...");
            progressDialog.show();
//            params = new HashMap<>();
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                bitmap = bitmap.createScaledBitmap(bitmap, 200, 100, false);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            Map<String, String> map = new HashMap<>();
            map.put(WS_eSignOrder.Entry_No_string, WSeSignOrder.Entry_No);
            NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdatePictureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
            // String base64 = convertImage(bitmap);
//            PictureService.process(this, uri.toString(), new PictureProcessHandler(this));

//            Map<String, String> map = new HashMap<>();
//            map.put(WS_eSignOrder.Entry_No_string, WSeSignOrder.Entry_No);
//            switch (type) {
//                case "Picture1":
//                    signedNow = "Picture1";
//                    params.put("eSignRec", WSeSignOrder.Key);
//                    params.put("driverSign", "");
//                    params.put("shippingSign", "");
//                    params.put("receiverSign", "");
//                    params.put("picture1", base64);
//                    params.put("picture2", "");
//                    params.put("picture3", "");
//                    params.put("comment", "");
//                    NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdatePictureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
//                    break;
//                case "Picture2":
//                    signedNow = "Picture2";
//                    params.put("eSignRec", WSeSignOrder.Key);
//                    params.put("driverSign", "");
//                    params.put("shippingSign", "");
//                    params.put("receiverSign", "");
//                    params.put("picture1", "");
//                    params.put("picture2", base64);
//                    params.put("picture3", "");
//                    params.put("comment", "");
//                    NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdatePictureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
//                    break;
//                case "Picture3":
//                    signedNow = "Picture3";
//                    HashMap<String, String> param2 = new HashMap<>();
//                    params.put("eSignRec", WSeSignOrder.Key);
//                    params.put("driverSign", "");
//                    params.put("shippingSign", "");
//                    params.put("receiverSign", "");
//                    params.put("picture1", "");
//                    params.put("picture2", "");
//                    params.put("picture3", base64);
//                    params.put("comment", "");
//                    NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new UpdatePictureHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
//                    break;
//            }


        } else {
            Toast.makeText(this, "Please capture a Image using Picture Icon", Toast.LENGTH_LONG).show();

        }

    }

    @OnClick({R.id.postButton, R.id.printButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.postButton:
                progressDialog.setMessage("Posting Order....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Map<String, String> map = new HashMap<>();
                map.put(WS_PostSalesOrder.Document_Type_String, "1");
                map.put(WS_PostSalesOrder.No_String, WSeSignOrder.No);
                NetworkService.SOAP("Read", this, new GetSalesHeaderKeyHandler(new WeakReference<SalesOrderActivity>(this)), WS_PostSalesOrder.class, null, map);
                break;
            case R.id.printButton:
                if(new NetworkCheck().ConnectivityCheck(this)){
                    //new DownloadPDFTask().execute();

                    progressDialog.setMessage("Fetching record ....");
                    progressDialog.show();
                    Map<String, String> inputParams = new HashMap<>();
                    inputParams.put(WS_eSignOrder.Entry_No_string, WSeSignOrder.Entry_No);
                    NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new GetPostedeSignOrderRecordHandler(new WeakReference<SalesOrderActivity>(this)), WS_PostedeSignOrder.class, null, inputParams);

                    /*progressDialog.setMessage("Generating PDF....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
*/
                    //NetworkService.SOAP("GenerateReport", this, new GetPDFHeaderKeyHandler(new WeakReference<SalesOrderActivity>(this)), WS_GetPDFContent.class, null, keymap);

                    /*Map<String, String> keymap = new HashMap<>();
                    keymap.put("eSignRec", WSeSignOrder.Key);
                    NetworkService.SOAP(NavionUrl.WS_eSignOrder.GETPICTURE, this, new GetPictureCompleteHandler(new WeakReference<>(this)), WS_eSignOrder.class, null, keymap);*/

                    /*Map<String, String> keymap = new HashMap<>();
                    keymap.put("precSalesShipment", "300;bgAAAACJ/1NTODgxOTgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=8;582167820;");
                    keymap.put("shipmentNo", "SS88198");

                    NetworkService.SOAP(NavionUrl.WS_eSignOrder.SALESSHIPMENT, this, new GetPostSalesShipmentHandler(new WeakReference<>(this)), WS_SalesShipment.class, null, keymap);*/

                    /*Map<String, String> keymap = new HashMap<>();
                    keymap.put("eSignRec", WSeSignOrder.Key);
                    NetworkService.SOAP(NavionUrl.WS_eSignOrder.SENDPICTURE, this, new SendPictureHandler(new WeakReference<>(this)), WS_eSignOrder.class, null, keymap);*/
                }else{
                    Toast.makeText(this,"Internet connection not available",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //Begin #1 Nov-Dec 2017 ,Download PDF
    public class DownloadPDFTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog.setMessage("Downloading report please wait !!!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String status = "";
            try {
                status = downloadAndSaveFile(arg0[0]);
                //status = downloadAndSaveFile(getResources().getString(R.string.ftp_server), 21, getResources().getString(R.string.ftp_user_name), getResources().getString(R.string.ftp_password), getResources().getString(R.string.ftp_data_path));
                return status;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "Unable to download report";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.i("result", result + " ");
            if (result.equalsIgnoreCase("Success")) {
                File root = new File(Environment
                        .getExternalStorageDirectory()
                        + File.separator + "tempPDFdir" + File.separator);
                final File local_file = new File(root, "temp.pdf");
                if(local_file.exists()){
                    Toast.makeText(SalesOrderActivity.this,"PDF found attempt to print",Toast.LENGTH_SHORT).show();
                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                        String jobName = getString(R.string.app_name) + " Document";
                        PrintDocumentAdapter pda = new PrintDocumentAdapter() {
                            @Override
                            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                                InputStream input = null;
                                OutputStream output = null;
                                try {
                                    input = new FileInputStream(local_file);
                                    output = new FileOutputStream(destination.getFileDescriptor());
                                    byte[] buf = new byte[1024];
                                    int bytesRead;

                                    while ((bytesRead = input.read(buf)) > 0) {
                                        output.write(buf, 0, bytesRead);
                                    }

                                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                                } catch (FileNotFoundException ee) {
                                    //Catch exception
                                } catch (Exception e) {
                                    //Catch exception
                                } finally {
                                    try {
                                        input.close();
                                        output.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                                if (cancellationSignal.isCanceled()) {
                                    callback.onLayoutCancelled();
                                    return;
                                }
                                // int pages = computePageCount(newAttributes);
                                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                                callback.onLayoutFinished(pdi, true);
                            }
                        };
                        printManager.print(jobName, pda, null);
                    }
                }else{
                    Toast.makeText(SalesOrderActivity.this,"file not found",Toast.LENGTH_SHORT).show();
                }
            } else {
                android.app.AlertDialog.Builder mAlertDialog = new android.app.AlertDialog.Builder(SalesOrderActivity.this);
                mAlertDialog.setTitle("Failure");
                mAlertDialog.setCancelable(false);
                mAlertDialog.setMessage(result + " Please try after some time");
                mAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });
                mAlertDialog.show();
            }
        }

    }

    private String downloadAndSaveFile(String salesShipmentNumber){
        try {
            final URL downloadFileUrl = new URL("http://50.250.174.225:8085/"+salesShipmentNumber+".pdf");
            final HttpURLConnection urlConnection = (HttpURLConnection) downloadFileUrl.openConnection();
            SharedPreferences sharedPreferences = GlobalMethods.getSharedPrefrence(getApplicationContext());
            final String username = sharedPreferences.getString(Constants.USER, "");
            final String password = sharedPreferences.getString(Constants.PASSWORD, "");
            Authenticator.setDefault (new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    Log.i("authenticatoin","called");
                    return new PasswordAuthentication (username, password.toCharArray());
                }
            });
            File root = new File(Environment
                    .getExternalStorageDirectory()
                    + File.separator + "tempPDFdir" + File.separator);
            root.mkdirs();
            File local_file = new File(root, "temp.pdf");
            final FileOutputStream fileOutputStream = new FileOutputStream(local_file);
            final byte buffer[] = new byte[16 * 1024];

            InputStream inputStream;

            int status = urlConnection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {
                fileOutputStream.flush();
                fileOutputStream.close();
                return status+" " + (!TextUtils.isEmpty(urlConnection.getResponseMessage())? urlConnection.getResponseMessage():" ");
            }else {
                inputStream = urlConnection.getInputStream();
                int len1 = 0;
                while ((len1 = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len1);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                return "Success";
            }
        } catch (final Exception exception) {
            Log.i("Exception", "doInBackground - exception" + exception.getMessage());
            exception.printStackTrace();
            return "Failed to download PDF";
        }
    }
    //End #1 Nov-Dec 2017 ,Download PDF

    private String downloadAndSaveFile(String server, int portNumber,
                                       String user, String password, String filepath)
            throws IOException {
        FTPClient ftp = null;
        boolean success = false;
        File local_file = null;
        FTPFile ftpFile;
        String file_name = null;
        try {
            ftp = new FTPClient();
            //ftp.setConnectTimeout(600000);
            ftp.connect(server);
            //ftp.connect(server);
            Log.d(LOG_TAG, "Connected. Reply: " + ftp.getReplyString());

            ftp.login(user, password);
            Log.d(LOG_TAG, "Logged in");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //ftp.setFileTransferMode(FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE);
            Log.d(LOG_TAG, "Downloading");
            ftp.enterLocalPassiveMode();

            File root = new File(Environment
                    .getExternalStorageDirectory()
                    + File.separator + "tempPDFdir" + File.separator);
            root.mkdirs();
            local_file = new File(root, "temp.pdf");

            OutputStream outputStream = null;

            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(local_file));
                success = ftp.retrieveFile(filepath, outputStream);

            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }

            return success == true ? "Success" : "Failed to download PDF";

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to download PDF";
        } finally {
            if (ftp != null) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }



//    @Override
//    public void HideFab() {
//        Animation shake = AnimationUtils.loadAnimation(this, R.anim.left_right);
//        floatingActionButton.startAnimation(shake);
//        shake.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                floatingActionButton.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//    }

//    @Override
//    public void ShowFab() {
//        floatingActionButton.setVisibility(View.VISIBLE);
//        Animation shake = AnimationUtils.loadAnimation(this, R.anim.right_left);
//        floatingActionButton.startAnimation(shake);
//        shake.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                floatingActionButton.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        if (id == R.id.refresh) {
            progressDialog.setMessage("Loading Data ....");
            progressDialog.show();
            Map<String, String> map = new HashMap<>();
            map.put(WS_eSignOrder.Entry_No_string, WSeSignOrder.Entry_No);
            NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, this, new GetRecordHandler(new WeakReference<SalesOrderActivity>(this)), WS_eSignOrder.class, null, map);
        }
        return super.onOptionsItemSelected(item);
    }

    static class GetSalesHeaderKeyHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public GetSalesHeaderKeyHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                int args = msg.arg1;
                switch (args) {
                    case 1:
                        Bundle bundle = msg.getData();
                        WS_PostSalesOrder ws_PostSalesOrder = (WS_PostSalesOrder) bundle.getSerializable(Constants.MESSAGE);
                        salesOrderActivity.ws_postSalesOrder = ws_PostSalesOrder;
                        Map<String, String> map = new HashMap<>();
                        map.put("precSalesorder", ws_PostSalesOrder.Key);
                        salesOrderActivity.progressDialog.show();
                        NetworkService.SOAP("PostOrder", salesOrderActivity, new PostHandler(new WeakReference<SalesOrderActivity>(salesOrderActivity)), WS_PostSalesOrder.class, null, map);
                        break;
                    case 0:
                        String m = msg.getData().getString(Constants.MESSAGE);
                        if (m != null && !m.isEmpty()) {
                            Toast.makeText(salesOrderActivity, m, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(salesOrderActivity, "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        }
    }

    static class GetPDFHeaderKeyHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public GetPDFHeaderKeyHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                int args = msg.arg1;
                switch (args) {
                    case 1:
                        Bundle bundle = msg.getData();
                        WS_GetPDFContent ws_getPDFContent = (WS_GetPDFContent) bundle.getSerializable(Constants.MESSAGE);
                        if(ws_getPDFContent.getMessage() != null){
                            try {
                                File root = new File(Environment
                                        .getExternalStorageDirectory()
                                        + File.separator + "tempPDFdir" + File.separator);
                                root.mkdirs();
                                final File local_file = new File(root, "temp_base64.pdf");
                                FileOutputStream fos = new FileOutputStream(local_file);
                                fos.write(Base64.decode(ws_getPDFContent.getMessage(), Base64.NO_WRAP));
                                fos.close();
                                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    PrintDocumentAdapter pda = new PrintDocumentAdapter() {
                                        @Override
                                        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                                            InputStream input = null;
                                            OutputStream output = null;
                                            try {
                                                input = new FileInputStream(local_file);
                                                output = new FileOutputStream(destination.getFileDescriptor());
                                                byte[] buf = new byte[1024];
                                                int bytesRead;

                                                while ((bytesRead = input.read(buf)) > 0) {
                                                    output.write(buf, 0, bytesRead);
                                                }

                                                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                                            } catch (FileNotFoundException ee) {
                                                //Catch exception
                                            } catch (Exception e) {
                                                //Catch exception
                                            } finally {
                                                try {
                                                    input.close();
                                                    output.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                                            if (cancellationSignal.isCanceled()) {
                                                callback.onLayoutCancelled();
                                                return;
                                            }
                                            // int pages = computePageCount(newAttributes);
                                            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                                            callback.onLayoutFinished(pdi, true);
                                        }
                                    };
                                    printManager.print(jobName, pda, null);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(salesOrderActivity, "PDF content is empty", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case 0:
                        String m = msg.getData().getString(Constants.MESSAGE);
                        if (m != null && !m.isEmpty()) {
                            Toast.makeText(salesOrderActivity, m, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(salesOrderActivity, "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        }
    }

    static class GetPostSalesShipmentHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public GetPostSalesShipmentHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                int args = msg.arg1;
                switch (args) {
                    case 1:
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            Toast.makeText(salesOrderActivity, "Received : " + bundle.getString("base64","Nothing..."), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(salesOrderActivity, "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case 0:
                        String m = msg.getData().getString(Constants.MESSAGE);
                        if (m != null && !m.isEmpty()) {
                            Toast.makeText(salesOrderActivity, m, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(salesOrderActivity, "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        }
    }

    static class PostHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public PostHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                int args = msg.arg1;
                switch (args) {
                    case 3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(salesOrderActivity);
                        builder.setMessage("Posted Successfully");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //salesOrderActivity.onBackPressed();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                    case 0:
                        String errorMessage = msg.getData().getString(Constants.MESSAGE, "");
                        if (errorMessage.isEmpty()) {
                            errorMessage = "Something Went Wrong....";
                        }
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(salesOrderActivity);
                        builder1.setTitle("Error Posting ");
                        builder1.setMessage(errorMessage);
                        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        AlertDialog alertDialog1 = builder1.create();
                        alertDialog1.show();
                        break;

                }

            }
        }
    }

    class UpdateSignatureHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public UpdateSignatureHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                NetworkService.SOAP(NavionUrl.WS_eSignOrder.GETPICTURE, salesOrderActivity, new UpdatePictureCompleteHandler(new WeakReference<>(salesOrderActivity)), WS_eSignOrder.class, null, salesOrderActivity.params);
                salesOrderActivity.progressDialog.show();
            }
        }
    }

    class UpdatePictureHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public UpdatePictureHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                NetworkService.UploadPictureSOAP(NavionUrl.WS_eSignOrder.GETPICTURE, salesOrderActivity, new UpdatePictureCompleteHandler(new WeakReference<>(salesOrderActivity)), WS_eSignOrder.class, null, salesOrderActivity.params, uri, type, WSeSignOrder.Key);
                salesOrderActivity.progressDialog.show();
            }
        }
    }

    class SendPictureHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public SendPictureHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                int args = msg.arg1;
                switch (args) {
                    case 1:
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            Toast.makeText(salesOrderActivity, "Received : " + bundle.getString("BOLPDF","Nothing..."), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(salesOrderActivity, "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case 0:
                        String m = msg.getData().getString(Constants.MESSAGE);
                        if (m != null && !m.isEmpty()) {
                            Toast.makeText(salesOrderActivity, m, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(salesOrderActivity, "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        }
    }

    class UpdatePictureCompleteHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public UpdatePictureCompleteHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                Map<String, String> map = new HashMap<>();
                map.put(WS_eSignOrder.Entry_No_string, salesOrderActivity.WSeSignOrder.Entry_No);
                switch (msg.arg1) {
                    case 1:
                        try {
                            String base64String = msg.getData().getString("base64");
                            File root = new File(Environment
                                    .getExternalStorageDirectory()
                                    + File.separator + "tempPDFdir" + File.separator);
                            root.mkdirs();
                            File local_file = new File(root, "temp_base64.jpg");
                            FileOutputStream fos = new FileOutputStream(local_file);
                            fos.write(Base64.decode(base64String, Base64.NO_WRAP));
                            fos.close();
                            Toast.makeText(salesOrderActivity, "image generated", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        salesOrderActivity.progressDialog.show();
                        NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, salesOrderActivity, new GetRecordHandler(new WeakReference<SalesOrderActivity>(salesOrderActivity)), WS_eSignOrder.class, null, map);
                        break;
                    case 0:
                        salesOrderActivity.progressDialog.show();
                        String errorMessage = msg.getData().getString(Constants.MESSAGE, "");
                        if (errorMessage.isEmpty()) {
                            errorMessage = "Something Went Wrong....";
                        }
                        Toast.makeText(salesOrderActivity, errorMessage, Toast.LENGTH_LONG).show();
                        NetworkService.SOAP(NavionUrl.WS_eSignOrder.METHOD_Read, salesOrderActivity, new GetRecordHandler(new WeakReference<SalesOrderActivity>(salesOrderActivity)), WS_eSignOrder.class, null, map);
                        break;
                }
            }
        }
    }

    class GetPictureCompleteHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public GetPictureCompleteHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                switch (msg.arg1) {
                    case 1:
                        try {
                            String base64String = msg.getData().getString("base64");
                            File root = new File(Environment
                                    .getExternalStorageDirectory()
                                    + File.separator + "tempPDFdir" + File.separator);
                            root.mkdirs();
                            File local_file = new File(root, "temp_base64.jpg");
                            FileOutputStream fos = new FileOutputStream(local_file);
                            fos.write(Base64.decode(base64String, Base64.NO_WRAP));
                            fos.close();
                            Toast.makeText(salesOrderActivity, "image generated", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    case 3:
                        Toast.makeText(SalesOrderActivity.this,"success",Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        String errorMessage = msg.getData().getString(Constants.MESSAGE, "");
                        if (errorMessage.isEmpty()) {
                            errorMessage = "Something Went Wrong....";
                        }
                        Toast.makeText(salesOrderActivity, errorMessage, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

    class GetRecordHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public GetRecordHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                salesOrderActivity.progressDialog.dismiss();
                int args = msg.arg1;
                switch (args) {
                    case 1:
                        Bundle bundle = msg.getData();
                        WS_eSignOrder ws_eSignOrder = (WS_eSignOrder) bundle.getSerializable(Constants.MESSAGE);
                        salesOrderActivity.WSeSignOrder = ws_eSignOrder;
                        if (Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Driver_Signature_Signed) && (Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Shipping_Clerk_Signature_Sgd))) {
                            salesOrderActivity.postButton.setEnabled(true);
                            salesOrderActivity.printButton.setEnabled(true);
                            salesOrderActivity.postButton.setTextColor(Color.WHITE);
                            salesOrderActivity.printButton.setTextColor(Color.WHITE);
                        } else {
                            salesOrderActivity.postButton.setEnabled(false);
                            salesOrderActivity.postButton.setTextColor(Color.GRAY);
                            salesOrderActivity.printButton.setTextColor(Color.GRAY);
                            salesOrderActivity.printButton.setEnabled(false);
                        }
                        if (!salesOrderActivity.signedNow.isEmpty()) {
                            switch (salesOrderActivity.signedNow) {
                                case "Driver":
                                    SignFragment fragment = (SignFragment) salesOrderActivity.viewPagerAdapter.fragmentHashMap.get(salesOrderActivity.signedNow);
                                    if (fragment != null && salesOrderActivity.WSeSignOrder.Driver_Signature_Signed != null) {
                                        fragment.signed = Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Driver_Signature_Signed);
                                        fragment.makeSigned();
                                    }
                                    break;
                                case "Shipping clerk":
                                    SignFragment shippingSignFragment = (SignFragment) salesOrderActivity.viewPagerAdapter.fragmentHashMap.get(salesOrderActivity.signedNow);
                                    if (shippingSignFragment != null && salesOrderActivity.WSeSignOrder.Shipping_Clerk_Signature_Sgd != null) {
                                        shippingSignFragment.signed = Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Shipping_Clerk_Signature_Sgd);
                                        shippingSignFragment.makeSigned();
                                    }
                                    break;
                                case "Receiver":
                                    SignFragment reviverSignFragment = (SignFragment) salesOrderActivity.viewPagerAdapter.fragmentHashMap.get(salesOrderActivity.signedNow);
                                    if (reviverSignFragment != null && salesOrderActivity.WSeSignOrder.Receiver_Signature_Signed != null) {
                                        reviverSignFragment.signed = Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Receiver_Signature_Signed);
                                        reviverSignFragment.makeSigned();
                                    }
                                    break;
                                case "Picture1":
                                    PictureFragment picture1Fragment = (PictureFragment) salesOrderActivity.viewPagerAdapter.fragmentHashMap.get(salesOrderActivity.signedNow);
                                    if (picture1Fragment != null && salesOrderActivity.WSeSignOrder.Picture1_Signed != null) {
                                        picture1Fragment.mParam2 = Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Picture1_Signed);
                                        picture1Fragment.makeSigned();
                                    }
                                    break;
                                case "Picture2":
                                    PictureFragment picture2Fragment = (PictureFragment) salesOrderActivity.viewPagerAdapter.fragmentHashMap.get(salesOrderActivity.signedNow);
                                    if (picture2Fragment != null && salesOrderActivity.WSeSignOrder.Picture2_Signed != null) {
                                        picture2Fragment.mParam2 = Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Picture2_Signed);
                                        picture2Fragment.makeSigned();
                                    }
                                    break;
                                case "Picture3":
                                    PictureFragment picture3Fragment = (PictureFragment) salesOrderActivity.viewPagerAdapter.fragmentHashMap.get(salesOrderActivity.signedNow);
                                    if (picture3Fragment != null && salesOrderActivity.WSeSignOrder.Picture3_Signed != null) {
                                        picture3Fragment.mParam2 = Boolean.parseBoolean(salesOrderActivity.WSeSignOrder.Picture3_Signed);
                                        picture3Fragment.makeSigned();
                                    }
                                    break;
                                default:

                            }

                        }
                        break;
                    case 0:
                        Toast.makeText(salesOrderActivity, "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG);
                        Intent intent = new Intent(salesOrderActivity, SalesOrderListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        salesOrderActivity.startActivity(intent);
                        salesOrderActivity.finish();
                        break;
                }
            }
        }
    }

    class GetPostedeSignOrderRecordHandler extends Handler {
        WeakReference<SalesOrderActivity> salesOrderActivityWeakReference;

        public GetPostedeSignOrderRecordHandler(WeakReference<SalesOrderActivity> salesOrderActivityWeakReference) {
            this.salesOrderActivityWeakReference = salesOrderActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SalesOrderActivity salesOrderActivity = salesOrderActivityWeakReference.get();
            if (salesOrderActivity != null) {
                int args = msg.arg1;
                switch (args) {
                    case 1:
                        Bundle bundle = msg.getData();
                        WS_PostedeSignOrder ws_PostedeSignOrder = (WS_PostedeSignOrder) bundle.getSerializable(Constants.MESSAGE);

                        if (ws_PostedeSignOrder.getSales_Shipment_No() != null && !ws_PostedeSignOrder.getSales_Shipment_No().isEmpty()) {
                            Map<String, String> inputParams = new HashMap<>();
                            inputParams.put(WS_eSignOrder.ORDER_NO, ws_PostedeSignOrder.getSales_Shipment_No());
                            NetworkService.SOAP("PDFConverter", SalesOrderActivity.this, new GetPDFHeaderKeyHandler(new WeakReference<SalesOrderActivity>(SalesOrderActivity.this)), WS_GetPDFContent.class, null, inputParams);
                            // new DownloadPDFTask().execute(ws_PostedeSignOrder.getSales_Shipment_No());
                        }else{
                            salesOrderActivity.progressDialog.dismiss();
                            Toast.makeText(salesOrderActivity, "Sales Shipment Number not found", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 0:
                        salesOrderActivity.progressDialog.dismiss();
                        //new DownloadPDFTask().execute("");
                        Toast.makeText(salesOrderActivity, "No Response, Please Try Again", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        salesOrderActivity.progressDialog.dismiss();
                        break;
                }
            }else{
                salesOrderActivity.progressDialog.dismiss();
                Toast.makeText(salesOrderActivity, "Please Try Again", Toast.LENGTH_LONG).show();
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<String> fragments;
        HashMap<String, Fragment> fragmentHashMap = new HashMap<>();

        public ViewPagerAdapter(FragmentManager fm, ArrayList<String> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            String f = fragments.get(position);
            Fragment fragment = new Fragment();
            switch (f) {
                case "Driver":
                    fragment = SignFragment.newInstance(fragments.get(position), Boolean.parseBoolean(WSeSignOrder.Driver_Signature_Signed));
                    fragmentHashMap.put("Driver", fragment);
                    break;
                case "Shipping clerk":
                    fragment = SignFragment.newInstance(fragments.get(position), Boolean.parseBoolean(WSeSignOrder.Shipping_Clerk_Signature_Sgd));
                    fragmentHashMap.put("Shipping clerk", fragment);
                    break;
                case "Receiver":
                    fragment = SignFragment.newInstance(fragments.get(position), Boolean.parseBoolean(WSeSignOrder.Receiver_Signature_Signed));
                    fragmentHashMap.put("Receiver", fragment);
                    break;
                case "Picture1":
                    fragment = PictureFragment.newInstance(fragments.get(position), Boolean.parseBoolean(WSeSignOrder.Picture1_Signed));
                    fragmentHashMap.put("Picture1", fragment);
                    break;
                case "Picture2":
                    fragment = PictureFragment.newInstance(fragments.get(position), Boolean.parseBoolean(WSeSignOrder.Picture2_Signed));
                    fragmentHashMap.put("Picture2", fragment);
                    break;
                case "Picture3":
                    fragment = PictureFragment.newInstance(fragments.get(position), Boolean.parseBoolean(WSeSignOrder.Picture3_Signed));
                    fragmentHashMap.put("Picture3", fragment);
                    break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position);
        }


    }
}
