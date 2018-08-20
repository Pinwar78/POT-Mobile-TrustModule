package com.example.osbg.pot.services;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class NodeConnector {
    private Context context;
    private String nodeQrData;
    private String uuid = "";
    private String nodePubKey = "";
    private String host = "";
    private String pubId = "";

    private MessagingService messagingService;
    private NodeSettingsService nodeSettingsService;

    public NodeConnector(Context context, String nodeQrData) {
        this.context = context;
        this.nodeQrData = nodeQrData;
        getSettingsFromQrString();
        this.messagingService = new MessagingService(context);
        this.nodeSettingsService = new NodeSettingsService(context);
    }

    private void getSettingsFromQrString(){
        uuid = nodeQrData.substring(4, 40);
        nodePubKey = nodeQrData.substring(40, 256);
        host = nodeQrData.substring(256, nodeQrData.length());
    }

    private void saveNodeSettings(String uuid, String host, String pubKey, String moveKey) {
        this.nodeSettingsService.save("uuid", uuid);
        this.nodeSettingsService.save("host", host);
        this.nodeSettingsService.save("pubkey", pubKey);
        this.nodeSettingsService.save("movekey", moveKey);
        Toast.makeText(context, "Node settings applied!", Toast.LENGTH_SHORT).show();
    }

    public void connectToNode(final INodeRequestCallback callback) {
        try {
            JSONObject myDataToEncrypt = new JSONObject();
            myDataToEncrypt.put("pubid", messagingService.getPubId());
            myDataToEncrypt.put("uuid", uuid);

            String movekey = nodeSettingsService.get("movekey");
            if (movekey != null){
                myDataToEncrypt.put("movekey", movekey);
            }

            this.nodeSettingsService.save("host", host);
            this.nodeSettingsService.save("pubkey", nodePubKey);

            NodeRequestService volleyData = new NodeRequestService(context, host, nodePubKey);
            volleyData.sendDataToNode( host,"/device/new", Request.Method.POST, myDataToEncrypt.toString(), new INodeRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        saveNodeSettings(uuid, host, nodePubKey, response.getString("movekey"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callback.onSuccess(response);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
