package com.abc.evpnfree.activity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.evpnfree.R;
import com.abc.evpnfree.adapter.ServerListAdapter;
import com.abc.evpnfree.model.Server;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import de.blinkt.openvpn.core.VpnStatus;

import static com.abc.evpnfree.Constant.UpgradePro;

public class VPNListActivity extends BaseActivity {
    private ListView listView;
    private ServerListAdapter serverListAdapter;
    private List<Server> countryList;
    private RelativeLayout homeContextRL;
    private PopupWindow popupWindow;
    public static final String EXTRA_COUNTRY = "country";
    private Server currentServer = null;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpnlist);

        homeContextRL = (RelativeLayout) findViewById(R.id.homeContextRL);
        countryList = dbHelper.getUniqueCountries();
        TextView button2 = (TextView) findViewById(R.id.homeBtnChooseCountry);
        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();

            }
        });

        String IPAddress = getLocalIpAddress();

        String countrys = getIntent().getStringExtra(MainActivity.EXTRA_COUNTRY);
        Locale l = new Locale("", countrys);
        String countryy = l.getDisplayCountry();
        TextView Ipaddress = (TextView) findViewById(R.id.ipaddress);
        Ipaddress.setText("IP Address : " + IPAddress);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setTitle(countryy);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
        procheck();

        if (!VpnStatus.isVPNActive())
            connectedServer = null;

        listView = (ListView) findViewById(R.id.list);
    }


    private void chooseCountry() {
        View view = initPopUp(R.layout.choose_country, 0.6f, 0.8f, 0.8f, 0.7f);

        final List<String> countryListName = new ArrayList<String>();
        for (Server server : countryList) {
            String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                    localeCountries.get(server.getCountryShort()) : server.getCountryLong();
            countryListName.add(localeCountryName);
        }

        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryListName);

        lvCountry.setAdapter(adapter);
        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onSelectCountry(countryList.get(position));
            }
        });

        popupWindow.showAtLocation(homeContextRL, Gravity.CENTER,0, 0);
    }

    private View initPopUp(int resourse,
                           float landPercentW,
                           float landPercentH,
                           float portraitPercentW,
                           float portraitPercentH) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourse, null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * landPercentW),
                    (int)(heightWindow * landPercentH)
            );
        } else {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * portraitPercentW),
                    (int)(heightWindow * portraitPercentH)
            );
        }


        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        return view;
    }

    private void onSelectCountry(Server server) {
        Intent intent = new Intent(getApplicationContext(), VPNListActivity.class);
        intent.putExtra(EXTRA_COUNTRY, server.getCountryShort());
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        invalidateOptionsMenu();

        buildList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void ipInfoResult() {
        serverListAdapter.notifyDataSetChanged();
    }

    private void buildList() {
        String country = getIntent().getStringExtra(MainActivity.EXTRA_COUNTRY);
        final List<Server> serverList = dbHelper.getServersByCountryCode(country);
        serverListAdapter = new ServerListAdapter(this, serverList);

        TextView countryname = (TextView) findViewById(R.id.elapse);
        countryname.setText(country);

        String code = getIntent().getStringExtra(MainActivity.EXTRA_COUNTRY).toLowerCase();
        if (code.equals("do"))
            code = "dom";

        ((ImageView) findViewById(R.id.imgv))
                .setImageResource(
                        getResources().getIdentifier(code,
                                "drawable",
                                getPackageName()));

        listView.setAdapter(serverListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Server server = serverList.get(position);

                BaseActivity.sendTouchButton("detailsServer");
                Intent intent = new Intent(VPNListActivity.this, VPNInfoActivity.class);
                intent.putExtra(Server.class.getCanonicalName(), server);
                VPNListActivity.this.startActivity(intent);
            }
        });

        getIpInfo(serverList);
    }


    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void procheck()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean(UpgradePro, false);

        if(strPref)
        {
            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
            mAdMobAdView.setVisibility(View.GONE);


        }
        else {
            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdMobAdView.loadAd(adRequest);


        }

    }
}