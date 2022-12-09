package com.abc.evpnfree.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;


import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.evpnfree.adapter.HomeListAdapter;
import com.afollestad.materialdialogs.MaterialDialog;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.abc.evpnfree.BuildConfig;
import com.abc.evpnfree.R;

import com.abc.evpnfree.model.Server;
import com.abc.evpnfree.util.PropertiesService;
import com.google.android.gms.ads.MobileAds;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import static com.abc.evpnfree.Constant.UpgradePro;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    DecoView arcView, arcView2;
    public static final String EXTRA_COUNTRY = "country";
    private PopupWindow popupWindow;
    private LinearLayout homeContextRL;

    TextView centree;
    private List<Server> countryList;
    private ListView listView;
    private HomeListAdapter serverListAdapter;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeContextRL = (LinearLayout) findViewById(R.id.homeContextRL);
        countryList = dbHelper.getUniqueCountries();

        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
        initNavigationView();
      procheck();

        if (BaseActivity.connectedServer == null) {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");
            hello.setBackgroundResource(R.drawable.button2);
        }
        else {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("Connected");
            hello.setBackgroundResource(R.drawable.button3);
        }

        centree = (TextView) findViewById(R.id.centree);
        arcView = (DecoView) findViewById(R.id.dynamicArcView2);
        arcView2 = (DecoView) findViewById(R.id.dynamicArcView3);

        long totalServ = dbHelper.getCount();

        String totalServers = String.format(getResources().getString(R.string.total_servers), totalServ);
        centree.setText(totalServers);

        arcView2.setVisibility(View.VISIBLE);
        arcView.setVisibility(View.GONE);

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 0)
                .setInterpolator(new AccelerateInterpolator())
                .build());

        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00000000"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#ffffff"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        int series1Index2 = arcView.addSeries(seriesItem2);
        Random ran2 = new Random();
        int proc = ran2.nextInt(10) + 5;
        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(600)
                .build());


        arcView.addEvent(new DecoEvent.Builder(proc).setIndex(series1Index2).setDelay(2000).setListener(new DecoEvent.ExecuteEventListener() {
                                                                                                            @Override
                                                                                                            public void onEventStart(DecoEvent decoEvent) {


                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onEventEnd(DecoEvent decoEvent) {


                                                                                                                long totalServ = dbHelper.getCount();

                                                                                                                String totalServers = String.format(getResources().getString(R.string.total_servers), totalServ);
                                                                                                                centree.setText(totalServers);


                                                                                                            }
        }).build());



        listView = (ListView) findViewById(R.id.homeCountryList);
        String country = getIntent().getStringExtra(MainActivity.EXTRA_COUNTRY);
        final List<Server> serverList = dbHelper.getUniqueCountries();
        serverListAdapter = new HomeListAdapter(this, serverList);

        listView.setAdapter(serverListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelectCountry(countryList.get(position));
            }
        });


        Button button1 = (Button) findViewById(R.id.homeBtnRandomConnection);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                sendTouchButton("homeBtnRandomConnection");
                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toast.makeText(MainActivity.this, randomError, Toast.LENGTH_LONG).show();
                }



            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BaseActivity.connectedServer == null) {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");


            Button button1 = (Button) findViewById(R.id.homeBtnRandomConnection);
            hello.setText("Quick Connect");
            button1.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    sendTouchButton("homeBtnRandomConnection");
                    Server randomServer = getRandomServer();
                    if (randomServer != null) {
                        newConnecting(randomServer, true, true);
                    } else {
                        String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                        Toast.makeText(MainActivity.this, randomError, Toast.LENGTH_LONG).show();
                    }



                }
            });
        }
        else {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("Connected");
            hello.setBackgroundResource(R.drawable.button3);


            Button button1 = (Button) findViewById(R.id.homeBtnRandomConnection);
            button1.setText("Connected!");
            button1.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (connectedServer != null)
                        startActivity(new Intent(MainActivity.this, VPNInfoActivity.class));


                }
            });
        }

        invalidateOptionsMenu();


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }



    @Override
    protected boolean useHomeButton() {
        return true;
    }

    public void homeOnClick(View view) {
        switch (view.getId()) {
            case R.id.homeBtnChooseCountry:
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();
                break;
            case R.id.homeBtnRandomConnection:
                sendTouchButton("homeBtnRandomConnection");
                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toast.makeText(this, randomError, Toast.LENGTH_LONG).show();
                }
                break;
        }

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


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    private void initNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_speedtest) {
            startActivity(new Intent(this, SpeedTestActivity.class));


        }  else if (id == R.id.nav_home){
            startActivity(new Intent(this, MainActivity.class));
        }  else if (id == R.id.nav_vpnlist){
        }


        else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Best Free Vpn app download now. https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }else if (id == R.id.rate_us) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
        }
        else if (id == R.id.about_me) {
            aboutMyApp();

        }
        else if (id == R.id.nav_speedbooster) {
            startFragmentActivity(FragmentWrapperActivity.BOOSTER_CODE);

        }


        else if (id == R.id.privacypolicy) {
            startActivity(new Intent(MainActivity.this, TOSActivity.class));

        }

        else if (id == R.id.moreapp) {

            Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=8774282826145816300");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=8774282826145816300")));
            }


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void startFragmentActivity (String REQUEST_ACTIVITY_CODE) {
        Intent intent = new Intent (getApplicationContext(), FragmentWrapperActivity.class);
        intent.putExtra(FragmentWrapperActivity.REQUEST_ACTIVITY_CODE, REQUEST_ACTIVITY_CODE);
        startActivity (intent);
    }

    private void startFragmentActivity (String REQUEST_ACTIVITY_CODE, String RUNTIME_MODE) {
        Intent intent = new Intent (getApplicationContext(), FragmentWrapperActivity.class);
        intent.putExtra(FragmentWrapperActivity.REQUEST_ACTIVITY_CODE, REQUEST_ACTIVITY_CODE);
        intent.putExtra(FragmentWrapperActivity.RUNTIME_MODE, RUNTIME_MODE);
        startActivity (intent);
    }
    private void aboutMyApp() {

        MaterialDialog.Builder bulder = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .customView(R.layout.about, true)
                .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .titleColorRes(android.R.color.white)
                .positiveText("MORE APPS")
                .positiveColor(getResources().getColor(android.R.color.white))
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .limitIconToDefaultSize()
                .onPositive((dialog, which) -> {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=8774282826145816300");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/dev?id=8774282826145816300")));
                    }
                });

        MaterialDialog materialDialog = bulder.build();

        TextView versionCode = (TextView) materialDialog.findViewById(R.id.version_code);
        TextView versionName = (TextView) materialDialog.findViewById(R.id.version_name);
        versionCode.setText(String.valueOf("Version Code : " + BuildConfig.VERSION_CODE));
        versionName.setText(String.valueOf("Version Name : " + BuildConfig.VERSION_NAME));

        materialDialog.show();
    }

    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        });
        toggle.syncState();
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        return toolbar;
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



