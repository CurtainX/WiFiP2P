package com.shengxiao.wifip2p;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiP2pManager wifiP2pManager;
    IntentFilter intentFilter;
    WifiP2pManager.Channel channel;
    WifiP2pManager.PeerListListener peerListListener;
    static List<WifiP2pDevice> peers;
    static LinearLayout linearLayout;
    static int colorCode=0;
    Button searchbtn;
    static Button controllerbtn;
    RecyclerView devicesHolder;
    WifiP2pInfo mInfo;
    WifiP2pManager.ConnectionInfoListener myConnectionInfoListener;
    ProgressDialog progressDialog;
    DeviceListAdapter deviceListAdapter;
    private String TAG="Log2";
    WiFiDirectBroadcastReceiver wiFiDirectBroadcastReceiver;
    SeekBar seekBarR,seekBarG,seekBarB;

    LinearLayout controllerGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        wifiP2pManager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(getApplicationContext(), getMainLooper(), null);

        peers=new ArrayList<WifiP2pDevice>();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        devicesHolder=(RecyclerView)findViewById(R.id.device_list_holder);
        devicesHolder.setLayoutManager(linearLayoutManager);


        linearLayout=(LinearLayout)findViewById(R.id.bulb);
        devicesHolder.setHasFixedSize(true);
        linearLayout.setBackgroundColor(Color.BLACK);

        controllerGroup=(LinearLayout)findViewById(R.id.controllerContainer);
        controllerGroup.setVisibility(View.GONE);
        seekBarR=(SeekBar)findViewById(R.id.skbR);
        seekBarG=(SeekBar)findViewById(R.id.skbG);
        seekBarB=(SeekBar)findViewById(R.id.skbB);

        seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller(progress,seekBarG.getProgress(),seekBarB.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller(seekBarR.getProgress(),progress,seekBarB.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller(seekBarR.getProgress(),seekBarG.getProgress(),progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        searchbtn=(Button)findViewById(R.id.search_device);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        controllerbtn=(Button)findViewById(R.id.on_off);
        controllerbtn.setVisibility(View.GONE);
        controllerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controllerGroup.getVisibility()==View.GONE){
                    controllerGroup.setVisibility(View.VISIBLE);
                }
                else {
                    controllerGroup.setVisibility(View.GONE);
                }

            }
        });

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Connecting");

        peerListListener=new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                deviceListAdapter=new DeviceListAdapter(peers,getApplicationContext(),wifiP2pManager,channel,progressDialog);
                devicesHolder.setAdapter(deviceListAdapter);
                Log.d("Log",peerList.getDeviceList().size()+"");
            }
        };

        myConnectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {

                if (info!=null)
                {
                    mInfo = info;

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    TextView view = (TextView) MainActivity.this.findViewById(R.id.group_owner_yn);
                    view.setText("Group Owner: " + ((info.isGroupOwner == true) ? "Yes" : "No"));

                    view = (TextView) MainActivity.this.findViewById(R.id.owner_ip);
                    view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

                    if (info.groupFormed && info.isGroupOwner) {
                        ((TextView) MainActivity.this.findViewById(R.id.server_client)).setText("Server");
                        //ServerAsyncTask
                        new Server(getApplicationContext()).execute();

                        controllerbtn.setVisibility(View.GONE);

                    } else if (info.groupFormed) {
                        controllerbtn.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        controllerbtn.setVisibility(View.VISIBLE);
                        ((TextView) MainActivity.this.findViewById(R.id.server_client)).setText("Client");
                    }

                }

            }
        };
        //registerReceiver(broadcastReceiver,intentFilter);
        wiFiDirectBroadcastReceiver=new WiFiDirectBroadcastReceiver(wifiP2pManager,channel);
        registerReceiver(wiFiDirectBroadcastReceiver,intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wiFiDirectBroadcastReceiver);

    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wiFiDirectBroadcastReceiver,intentFilter);
    }

    public void search(){
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "discover peers!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "discover peers Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void controller(int r,int g, int b){
        new Client(mInfo,getApplicationContext(),r,g,b).execute();
    }


    public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

        private WifiP2pManager mManager;
        private WifiP2pManager.Channel mChannel;

        public WiFiDirectBroadcastReceiver() {
        }

        public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel) {
            super();
            this.mManager = manager;
            this.mChannel = channel;

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                Log.d(TAG, "onReceive: WIFI_P2P_STATE_CHANGED_ACTION ");
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                Log.d(TAG, "onReceive: WIFI_P2P_PEERS_CHANGED_ACTION ");
                if (mManager != null) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                Log.d(TAG, "onReceive: WIFI_P2P_CONNECTION_CHANGED_ACTION");
                if (mManager == null) {
                    return;
                }
                NetworkInfo networkInfo = intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    // we are connected with the other device, request connection
                    // info to find group owner IP
                    Log.d(TAG, "onReceive: isConnected");
                    mManager.requestConnectionInfo(mChannel, myConnectionInfoListener);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Peer Connected!",Toast.LENGTH_SHORT).show();
                } else {
                    // It's a disconnect
                    Log.d(TAG, "onReceive: disconnect");
                    progressDialog.dismiss();
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing

                Log.d(TAG, "onReceive: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            }
        }
    }


}

