package com.example.osbg.pot.services.dapps;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DappFilesHandler {
    public static final String DAPP_PREFERENCES = "dapps";
    public static final String DAPP_DIRECTORY = "dapps";
    private Context context;

    public DappFilesHandler(Context context){
        this.context = context;
    }

    public File saveDataToFile(byte[] data, String name){
        File dappsDir = getDappsDir();

        File newFile = new File(dappsDir, name);
        try {
            newFile.createNewFile();
            FileOutputStream fileStream = new FileOutputStream(newFile);
            fileStream.write(data);
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("POTT ERROR", "CANT SAVE");
        }

        return newFile;
    }

    public File getDappsDir(){
        File baseDir = context.getFilesDir();
        File dappsDir = new File(baseDir, DAPP_DIRECTORY);

        if (!dappsDir.exists()){
            dappsDir.mkdir();
        }

        return dappsDir;
    }
}
