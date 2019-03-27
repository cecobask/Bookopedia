package ie.bask.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class BookScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private List<String> mPermDeniedList = new ArrayList<>();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Check permission to camera
        if (isPermGranted()) {
            // Initialize the scanner view
            mScannerView = new ZXingScannerView(this);
            setContentView(mScannerView);
        } else {
            mPermDeniedList.add(Manifest.permission.CAMERA);
            // Request camera permission
            requestPermissions(mPermDeniedList);
        }
    }

    private void requestPermissions(List<String> access) {
        // Convert List to Array for use in requesting permissions
        String[] stringArray = access.toArray(new String[0]);
        ActivityCompat.requestPermissions(this, stringArray, 1001);
    }

    // Method to check if a permission is granted
    private boolean isPermGranted() {
        return ContextCompat.checkSelfPermission(BookScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==  1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Initialize the scanner view
                mScannerView = new ZXingScannerView(this);
                setContentView(mScannerView);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mScannerView!=null) {
            // Register ourselves as a handler for scan results.
            mScannerView.setResultHandler(this);
            // Start camera on resume
            mScannerView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView!=null) {
            // Stop camera on pause
            mScannerView.stopCamera();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Log results
        Log.v("Bookopedia", rawResult.getText());
        Log.v("Bookopedia", rawResult.getBarcodeFormat().toString());
        // Put result as extra to Intent and go back to previous activity, where results will be handled
        Intent result = getIntent();
        result.putExtra("bar_code",rawResult.getText() );
        this.setResult(1000,result);
        // Close activity
        finish();
    }
}