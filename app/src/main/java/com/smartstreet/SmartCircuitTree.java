package com.smartstreet;


import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Singleton Circuit Tree which stores informaiton about the last scanned tree.
 */
public class SmartCircuitTree {
    private static SmartCircuitTree sInstance;

    // Scanned Bardcode content
    private String barcodeContent;

    // Pictures clicked
    private ArrayList<Bitmap> pictures = new ArrayList<>();

    // Comments made
    private ArrayList<String> comments = new ArrayList<>();

    public static synchronized SmartCircuitTree getInstance() {
        if (sInstance == null) {
            sInstance = new SmartCircuitTree();
        }
        return sInstance;
    }

    public static synchronized void clearInstance() {
        sInstance = null;
    }

    public void setBarcodeContent(String barcodeContent) {
        this.barcodeContent = barcodeContent;
    }

    public String getBarcodeContent() {
        return barcodeContent;
    }

    public void insertPicture(Bitmap newImage) {
        pictures.add(newImage);
    }

    public ArrayList<Bitmap> getPictures() {
        return pictures;
    }

    public void addComment(String comment) {
        if (!TextUtils.isEmpty(comment)) {
            comments.add(comment);
        }
    }

    public ArrayList<String> getComments() {
        return comments;
    }
}
