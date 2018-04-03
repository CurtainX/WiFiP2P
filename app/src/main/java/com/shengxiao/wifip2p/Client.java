package com.shengxiao.wifip2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by ShengXiao on 2018-03-28.
 */

public class Client extends AsyncTask<Void,Void,String> {
    WifiP2pInfo wifiP2pInfo;
    Socket socket;
    Context context;
    private String TAG="Log";
    int r,g,b;

    public Client(WifiP2pInfo wifiP2pInfo,Context context,int r,int g,int b) {
        this.wifiP2pInfo=wifiP2pInfo;
        socket = new Socket();
        this.context=context;
        this.r=r;
        this.g=g;
        this.b=b;
    }

    @Override

    protected String doInBackground(Void... voids) {

        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            Log.d(TAG, "Opening client socket -> ");

            socket.bind(null);
            socket.connect((new InetSocketAddress(wifiP2pInfo.groupOwnerAddress.getHostAddress(), 8988)), 2000);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(r);
            dos.writeInt(g);
            dos.writeInt(b);
            dos.flush();
            outputStream.close();
            dos.close();
            Log.d("Log",outputStream.toString());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context,"Success",Toast.LENGTH_LONG).show();
    }
}
