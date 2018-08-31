package com.example.osbg.pot.services.dapps;

import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.example.osbg.pot.services.KeyGenerator;
import com.example.osbg.pot.services.api.INodeRequestCallback;
import com.example.osbg.pot.services.api.NodeRequest;
import com.example.osbg.pot.utilities.FileHelper;
import com.example.osbg.pot.utilities.encryption.AESDecryptor;
import com.example.osbg.pot.utilities.encryption.AESEncryptor;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class DappLoader {

    private Context context;
    private String uuid;
    public DappFilesHandler filesHandler;

    public DappLoader(Context context, String uuid) {
        this.context = context;
        this.uuid = uuid;
        this.filesHandler = new DappFilesHandler(context);
    }

    public void loadDapp(){
        JSONObject requestJson = new JSONObject();

        final byte[] aeskey = KeyGenerator.genRandomBytes(AESEncryptor.AES_BYTES_NUM);
        final byte[] iv = KeyGenerator.genRandomBytes(AESEncryptor.AES_BYTES_NUM);

        try {
            requestJson.put("dapp_uuid", uuid);
            requestJson.put("aeskey", Base64.encodeToString(aeskey, Base64.DEFAULT).trim());
            requestJson.put("iv", Base64.encodeToString(iv, Base64.DEFAULT).trim());

            NodeRequest nodeRequest = new NodeRequest(context);
            nodeRequest.getBytes("/dapp/get_files", Request.Method.POST, requestJson.toString(), new INodeRequestCallback<byte[]>() {
                @Override
                public void onSuccess(byte[] response) {
                    // Saving encrypted zip
                    File encrZipFile = filesHandler.saveDataToFile(response, uuid+"_encr.zip");

                    // Decrypting the encrypted zip file
                    AESDecryptor aesDecryptor = new AESDecryptor(aeskey, iv);
                    File decrZipFile = new File(filesHandler.getDappsDir(), uuid+".zip");
                    try {
                        aesDecryptor.decryptFile(encrZipFile, decrZipFile);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Deleting the encrypted file
                    encrZipFile.delete();

                    // Unzipping the zip file
                    File dappDir = new File(filesHandler.getDappsDir(), uuid);

                    try {
                        FileHelper.unzip(decrZipFile, dappDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Deleting the zip file
                    decrZipFile.delete();
                    onDataLoaded(dappDir);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onDataLoaded(File dappDir){

    }
}
