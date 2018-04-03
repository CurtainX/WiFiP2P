package com.shengxiao.wifip2p;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by ShengXiao on 2018-03-28.
 */

public class Server extends AsyncTask<Void,Void,String> {
    String out;
    Context context;
    private String TAG="Log";
    int r=0,b=0,g=0;

    public Server(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Log.d(TAG, "doInBackground: serverSocket");
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
                ServerSocket serverSocket = new ServerSocket(8988);
                Socket client = serverSocket.accept();
                SocketAddress remoteSocketAddress = client.getRemoteSocketAddress();
                Log.d("Log",remoteSocketAddress.toString());

                DataInputStream datainputstream =new DataInputStream(client.getInputStream());
                r=datainputstream.readInt();
                g=datainputstream.readInt();
                b=datainputstream.readInt();
                datainputstream.close();
                serverSocket.close();
                Log.d("Log",out+"");

            //return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
            MainActivity.linearLayout.setBackgroundColor(Color.rgb(r,g,b));
        new Server(context).execute();
    }
}
