package com.smartstreet;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set Barcode button listener
        findViewById(R.id.button_bar_code).setOnClickListener(this);
        // Set Camera button listener
        findViewById(R.id.button_camera).setOnClickListener(this);
        // Set Maps button listener
        findViewById(R.id.button_maps).setOnClickListener(this);
        // Set Comments button listener
        findViewById(R.id.button_comments).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.button_bar_code : {
                startActivity(BarcodeReaderActivity.createIntent(this));
                break;
            }
            case R.id.button_camera : {
                startActivity(TakePictureActivity.createIntent(this));
                break;
            }
            case R.id.button_maps : {
                startActivity(MapsActivity.createIntent(this));
                break;
            }
            case R.id.button_comments : {
                startActivity(CommentsActivity.createIntent(this));
                break;
            }
            default: {
                break;
            }
        }
    }
}
