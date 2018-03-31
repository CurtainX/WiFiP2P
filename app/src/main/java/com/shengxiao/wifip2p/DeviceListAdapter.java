package com.shengxiao.wifip2p;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ShengXiao on 2018-03-30.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceHolder> {
    List<WifiP2pDevice> peers;
    Context context;
    WifiP2pManager wifiManager;
    WifiP2pManager.Channel channel;
    ProgressDialog progressDialog;
    public DeviceListAdapter(List<WifiP2pDevice> peers, Context context, WifiP2pManager wifiManager, WifiP2pManager.Channel channel,ProgressDialog progressDialog) {
        this.peers = peers;
        this.context=context;
        this.wifiManager=wifiManager;
        this.channel=channel;
        this.progressDialog=progressDialog;
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.device_item,parent,false);
        DeviceHolder vh=new DeviceHolder(view);
        return  vh;
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, final int position) {
        holder.devicename.setText(peers.get(position).deviceName);
        holder.deviceip.setText(peers.get(position).deviceAddress);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();

                if(peers.size()>0){
                    final WifiP2pDevice device = (WifiP2pDevice) peers.get(position);
                    final WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;
                    config.wps.setup = WpsInfo.PBC;

                    progressDialog.show();

                    wifiManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            // 连接成功
                            Toast.makeText(context, "Connect to " + device.deviceName + "Success!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int arg0) {
                            // 连接失败
                            Toast.makeText(context, "Connect to" + device.deviceName + "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(context,"One second",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    public class DeviceHolder extends RecyclerView.ViewHolder{
        TextView devicename,deviceip;
        LinearLayout linearLayout;

        public DeviceHolder(View itemView) {
            super(itemView);
            devicename=(TextView)itemView.findViewById(R.id.device_name);
            deviceip=(TextView)itemView.findViewById(R.id.device_ip);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.itemholder);
        }
    }
}
