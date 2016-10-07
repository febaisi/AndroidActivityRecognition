package com.febaisi.activityrecognition;

import android.content.Context;
import android.media.MediaScannerConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by BaisFe01 on 10/5/2016.
 */

public class Utils {

    public static String TAG = "ActivityRecognitionApp";

    public static void writeToSDFile(Context context, String message, String fileName){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/ActivityLogs");
        dir.mkdirs();
        File file = new File(dir, fileName + ".txt");
        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(message);
            pw.flush();
            pw.close();
            f.close();
            MediaScannerConnection.scanFile
                    (context, new String[] {file.toString()}, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
