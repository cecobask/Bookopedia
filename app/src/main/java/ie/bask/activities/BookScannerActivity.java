package ie.bask.activities;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by margarita on 11/28/18.
 */


public class BookScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private List<String> mPermDeniedList = new ArrayList<>();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        //check permission to camera
        if (isPermGranted(Manifest.permission.CAMERA)) {
            mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
            setContentView(mScannerView);                // Set the scanner view as the content view
        } else {
            mPermDeniedList.add(Manifest.permission.CAMERA);
            checkPermission(mPermDeniedList, 1001);
        }
    }
    private void checkPermission(List<String> access, int requestCode) {
        String[] stringArray = access.toArray(new String[access.size()]);
        ActivityCompat.requestPermissions(this, stringArray, requestCode);
    }

    private boolean isPermGranted(String perm) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(BookScannerActivity.this, perm) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==  1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
                setContentView(mScannerView);                // Set the scanner view as the content view
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mScannerView!=null) {
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();          // Start camera on resume
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView!=null) {
            mScannerView.stopCamera();// Stop camera on pause
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("Result", rawResult.getText()); // Prints scan results
        Log.v("Barcode", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        Intent result = getIntent();
        result.putExtra("bar_code",rawResult.getText() );
        this.setResult(1000,result);
        finish();
    }
}