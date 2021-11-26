package com.eziride.user.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.eziride.user.Helper.LocaleUtils;
import com.google.android.gms.vision.barcode.Barcode;
import com.eziride.user.Constants.Constants;
import com.eziride.user.R;

import static com.eziride.user.Constants.Constants.QRCODESCAN;

public class QRscan extends AppCompatActivity {
    public static final String BARCODE_KEY = "BARCODE";
    MaterialBarcodeScanner materialBarcodeScanner;
    private Barcode barcodeResult;
    String d_address,s_address;
    int SPECIFICDRIVER = 101;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        Intent i = getIntent();
        s_address = i.getStringExtra("s_address");
        d_address = i.getStringExtra("d_address");
        QRCODESCAN =false;

        if(savedInstanceState != null){
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if(restoredBarcode != null){
               // result.setText(restoredBarcode.rawValue);
                barcodeResult = restoredBarcode;
            }
        }
        startScan();
    }
    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(QRscan.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withActivity(this)
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                       // result.setText(barcode.rawValue);
                        Constants.QRANDDIRECT = false;
                        Intent i = new Intent(getApplicationContext(),CustomGooglePlacesSearch.class);
                        i.putExtra("qrcode",barcode.rawValue);
                        i.putExtra("cursor", "cursor");
                        i.putExtra("s_address", s_address);
                        i.putExtra("d_address", d_address);
                        startActivityForResult(i, SPECIFICDRIVER);
                        System.out.println("enter the qr code scanner output"+barcode.rawValue);

                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(QRCODESCAN){
            finish();
        }else {
          //  startScan();
            QRCODESCAN =true;
        }
      //  Toast.makeText(getApplicationContext(),"onresume",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan();
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

    @Override
    public void onBackPressed() {
        QRCODESCAN =true;
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK, data);
            finish();
        }else if(requestCode == Activity.RESULT_CANCELED){
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    //    Toast.makeText(getApplicationContext(),"OnPause",Toast.LENGTH_LONG).show();
    }
}
