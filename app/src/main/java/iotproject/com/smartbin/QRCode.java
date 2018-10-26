package iotproject.com.smartbin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import iotproject.com.smartbin.Utils.BaseActivity;

public class QRCode extends AppCompatActivity  {

    String areaId, binId;

    DatabaseReference emptyBinRef;

    CodeScannerView scannerView;

    private CodeScanner mCodeScanner;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        areaId = getIntent().getStringExtra("areaId");
        binId = getIntent().getStringExtra("binId");

        emptyBinRef = FirebaseDatabase.getInstance().getReference(areaId+"/bins/"+binId);

        scannerView = findViewById(R.id.scanner_view);

        activity = QRCode.this;

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ( binId.equals(result.getText()) ) {
                            Toast.makeText(activity, "Bin Emptied Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(activity, "Scanned Code of some other bin", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
