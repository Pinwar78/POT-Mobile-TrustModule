package com.example.osbg.pot.infrastructure;

import android.content.Context;

import com.android.volley.Request;
import com.example.osbg.pot.services.INodeRequestCallback;
import com.example.osbg.pot.services.NodeRequestService;
import com.example.osbg.pot.utilities.HashCalculator;

import org.json.JSONObject;

public class QrCodeGenerator {
    private Context context;

    public QrCodeGenerator(Context context){
        this.context = context;
    }

    public void generate(String data, INodeRequestCallback callback, boolean potsum){
        NodeRequestService volleyData = new NodeRequestService(context);
        try {
            if (potsum){
                String potMd5 = HashCalculator.calculatePotMD5(data);
                data = potMd5 + data;
            }

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("data", data);
            volleyData.sendDataToNode("/device/genqrcode", Request.Method.POST, jsonRequest.toString(), callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generate(String data, INodeRequestCallback callback){
        this.generate(data, callback, true);
    }
}
