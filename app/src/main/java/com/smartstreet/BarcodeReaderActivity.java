package com.smartstreet;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Activity that handles the barcode scanning and displays the scanned content.
 */
public class BarcodeReaderActivity extends FragmentActivity implements View.OnClickListener {
    private TextView mTreeInfoDisplay;

    public static Intent createIntent(Context context) {
        final Intent intent = new Intent(context, BarcodeReaderActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_reader);

        findViewById(R.id.button_scan_barcode).setOnClickListener(this);

        mTreeInfoDisplay = (TextView) findViewById(R.id.smart_tree_info);
        if (SmartCircuitTree.getInstance().getBarcodeContent() != null) {
            // If a barcode has already been scanned, display its content.
            mTreeInfoDisplay.setVisibility(View.VISIBLE);
            mTreeInfoDisplay.setText(SmartCircuitTree.getInstance().getBarcodeContent());
        } else {
            // If no barcode scanned currently, hide the info textview.
            mTreeInfoDisplay.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_scan_barcode) {
            try {
                // Intent for zxing barcode scanner app.
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException exception) {
                // Show Alert dialog to download zxing barcode scanner if activity to open the intent not found.
                new AlertDialog.Builder(this)
                        .setTitle("Barcode scanner is not installed")
                        .setMessage("Download barcode scanner?")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Playstore search query uri
                                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException exception) {

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Retrieve the scanned content from the bundle
                String contents = intent.getStringExtra("SCAN_RESULT");
                if (!TextUtils.isEmpty(contents)) {
                    // If non-empty content returned, remove the previously scanned smart tree and
                    // add the new smart tree info.
                    SmartCircuitTree.clearInstance();
                    SmartCircuitTree.getInstance().setBarcodeContent(contents);
                    // Display the contents of the newly scanned barcode.
                    mTreeInfoDisplay.setVisibility(View.VISIBLE);
                    mTreeInfoDisplay.setText(SmartCircuitTree.getInstance().getBarcodeContent());
                }
            }
        }
    }
}
