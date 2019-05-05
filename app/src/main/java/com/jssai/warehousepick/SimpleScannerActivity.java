package com.jssai.warehousepick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.Serializable;

import com.jssai.warehousepick.Model.WarehousePickItem;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "Barcode";
    private ZXingScannerView mScannerView;
    private boolean barcodeFound;
    private String barcode;
    public static int SCAN_PICK_LIST = 1;
    public static int SCAN_PICK_ITEM = 2;
    int purpose;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        purpose = getIntent().getIntExtra("purpose", 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        if (!barcodeFound) {
            barcodeFound = true;
            barcode = rawResult.getText();
            //barcode = "PS000841";
            validateBarcode();
            Log.v(TAG, rawResult.getText()); // Prints scan results
        }

        mScannerView.resumeCameraPreview(this);
    }

    private void validateBarcode() {
        if (purpose == SCAN_PICK_LIST && AppController.pickHashMap.keySet().contains(barcode)) {
            moveToWarehouseDetailPage(AppController.pickHashMap.get(barcode));
        } else if (purpose == SCAN_PICK_ITEM && AppController.pickItemsHashMap.keySet().contains(barcode)) {
            moveToWarehouseItemEditPage(AppController.pickItemsHashMap.get(barcode));
        } else {
            finish();
            Toast.makeText(this, "Scanned Item not available!", Toast.LENGTH_LONG).show();
        }
    }

    private void moveToWarehouseDetailPage(WarehousePickItem warehousePickItem) {
        finish();
        Intent intent = new Intent(this, WarehouseDetailPage.class);
        intent.putExtra("item", (Serializable) warehousePickItem);
        startActivity(intent);
    }

    private void moveToWarehouseItemEditPage(WarehousePickItem warehousePickItem) {
        finish();
        Intent intent = new Intent(this, WarehouseItemEditPage.class);
        intent.putExtra("item", (Serializable) warehousePickItem);
        startActivity(intent);
    }
}