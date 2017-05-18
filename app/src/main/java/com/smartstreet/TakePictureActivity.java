package com.smartstreet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity that handles the clicking and displaying of images.
 */
public class TakePictureActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private PicturesAdapter adapter;
    private ListView listView;

    public static Intent createIntent(Context context) {
        final Intent intent = new Intent(context, TakePictureActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        findViewById(R.id.button_take_picture).setOnClickListener(this);

        listView = (ListView) findViewById(R.id.images_listview);

        // Initialize the adapter from the already taken pictures.
        adapter = createLatestPicturesAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private PicturesAdapter createLatestPicturesAdapter() {
        Bitmap[] smartTreePictureArray =
                new Bitmap[SmartCircuitTree.getInstance().getPictures().size()];
        // Creates an adapter fromt he array of pictures stored in SmartCircuitTree's current
        // instance.
        return new PicturesAdapter(this, R.layout.row_picture_adapter,
                SmartCircuitTree.getInstance().getPictures().toArray(smartTreePictureArray));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_take_picture) {
            try {
                dispatchTakePictureIntent();
            } catch (IOException e) {
            }
        }
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.get("data") != null) {
                // Add the latest clicked image to the singleton and recreate the listview adapter.
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                SmartCircuitTree.getInstance().insertPicture(imageBitmap);
                adapter = createLatestPicturesAdapter();
                listView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Opens the share with multiple apps feature on clicking any image.
        final ImageView imageView = (ImageView) view.findViewById(R.id.picture);
        final Bitmap imageBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        if (imageBitmap != null) {
            try {
                // Create a temporary file to allow sharing of the stored bitmap.
                File file = new File(getExternalCacheDir(), new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".png");
                file.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                // Creates the Share intent
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    /**
     * The adapter that provides the images for the listview.
     */
    private class PicturesAdapter extends ArrayAdapter<Bitmap> {

        private Bitmap[] items;

        public PicturesAdapter(Context context, int textViewResourceId,
                               Bitmap[] items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_picture_adapter, null);
            }

            Bitmap it = items[position];
            if (it != null) {
                ImageView iv = (ImageView) v.findViewById(R.id.picture);
                if (iv != null) {
                    iv.setImageBitmap(it);
                }
            }

            return v;
        }
    }

}
