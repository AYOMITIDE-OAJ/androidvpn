package com.abc.evpnfree.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.evpnfree.R;
import com.abc.evpnfree.activity.BaseActivity;
import com.abc.evpnfree.model.Server;
import com.abc.evpnfree.util.ConnectionQuality;
import com.abc.evpnfree.util.CountriesNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Server> serverList = new ArrayList<Server>();
    private Context context;
    private Map<String, String> localeCountries;

    public HomeListAdapter(Context c, List<Server> serverList) {
        inflater = LayoutInflater.from(c);
        context = c;
        this.serverList =  serverList;
        localeCountries = CountriesNames.getCountries();
    }


    @Override
    public int getCount() {
        return serverList.size();
    }


    @Override
    public Server getItem(int position) {
        return serverList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {

        v = inflater.inflate(R.layout.home_vpn_row, parent, false);

        final Server server = getItem(position);

        String code = server.getCountryShort().toLowerCase();
        if (code.equals("do"))
            code = "dom";


        ((de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.imageFlag))
                .setImageResource(
                        context.getResources().getIdentifier(code,
                                "drawable",
                                context.getPackageName()));
        ((ImageView) v.findViewById(R.id.imageConnect))
                .setImageResource(
                        context.getResources().getIdentifier(ConnectionQuality.getConnectIcon(server.getQuality()),
                                "drawable",
                                context.getPackageName()));

        ((TextView) v.findViewById(R.id.textHostName)).setText(server.getHostName());
        ((TextView) v.findViewById(R.id.textIP)).setText(server.getIp());


        String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                localeCountries.get(server.getCountryShort()) : server.getCountryLong();
//        ((TextView) v.findViewById(R.id.textCountry)).setText(localeCountryName);
        ((TextView) v.findViewById(R.id.textCity)).setText(localeCountryName);
        if (BaseActivity.connectedServer != null && BaseActivity.connectedServer.getHostName().equals(server.getHostName())) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.activeServer));
        }

        if (server.getType() == 1) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.additionalServer));
        }

        return v;
    }

}