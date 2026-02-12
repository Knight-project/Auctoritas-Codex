package com.example.auctaritascodex;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WebAppInterface {
    Context mContext;
    WebAppInterface(Context c) { mContext = c; }

    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data, String mimeType, String filename) throws IOException {
        // 1. Clean the base64 string
        byte[] fileBytes = Base64.decode(base64Data.split(",")[1], Base64.DEFAULT);

        // 2. Define the path (Downloads folder)
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);

        // 3. Save the file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileBytes);
        fos.flush();
        fos.close();

        Toast.makeText(mContext, "File Downloaded to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }
}
