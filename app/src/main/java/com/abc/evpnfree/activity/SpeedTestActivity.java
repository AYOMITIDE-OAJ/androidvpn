package com.abc.evpnfree.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.abc.evpnfree.R;
import com.abc.evpnfree.dns.HttpDownloadTest;
import com.abc.evpnfree.dns.HttpUploadTest;
import com.abc.evpnfree.dns.PingTest;
import com.abc.evpnfree.model.GetSpeedTestHostsHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import static com.abc.evpnfree.Constant.UpgradePro;

public class SpeedTestActivity extends AppCompatActivity {
    static int position = 0;
    static int lastPosition = 0;
    DecoView arcView, arcView2;
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;

    @Override
    public void onResume() {
        super.onResume();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedtest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);


        final Button startButton = (Button) findViewById(R.id.startButton);
        final DecimalFormat dec = new DecimalFormat("#.##");
        startButton.setText("Begin Test");


        arcView = (DecoView) findViewById(R.id.dynamicArcView2);
        arcView2 = (DecoView) findViewById(R.id.dynamicArcView3);


        arcView2.setVisibility(View.VISIBLE);
        arcView.setVisibility(View.GONE);

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 247, 189, 214))
                .setRange(0, 100, 0)
                .setInterpolator(new AccelerateInterpolator())
                .build());


        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#f0a734"))
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


                //   long totalServ = dbHelper.getCount();

                //  String totalServers = String.format(getResources().getString(R.string.total_servers), totalServ);
                //   centree.setText(totalServers);


            }
        }).build());





        procheck();


        tempBlackList = new HashSet<>();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startButton.setEnabled(false);


                if (getSpeedTestHostsHandler == null) {
                    getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
                    getSpeedTestHostsHandler.start();
                }

                new Thread(new Runnable() {
                    RotateAnimation rotate;
                    ImageView barImageView = (ImageView) findViewById(R.id.barImageView);
                    TextView pingTextView = (TextView) findViewById(R.id.pingTextView);
                    TextView downloadTextView = (TextView) findViewById(R.id.downloadTextView);
                    TextView uploadTextView = (TextView) findViewById(R.id.uploadTextView);



                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startButton.setText("Selecting best server based on ping...");
                            }
                        });


                        int timeCount = 600;
                        while (!getSpeedTestHostsHandler.isFinished()) {
                            timeCount--;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
                            if (timeCount <= 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "No Connection...", Toast.LENGTH_LONG).show();
                                        startButton.setEnabled(true);
                                        startButton.setTextSize(16);
                                        procheck2();

                                        startButton.setText("Restart Test");
                                    }
                                });
                                getSpeedTestHostsHandler = null;
                                return;
                            }
                        }


                        HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
                        HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
                        double selfLat = getSpeedTestHostsHandler.getSelfLat();
                        double selfLon = getSpeedTestHostsHandler.getSelfLon();
                        double tmp = 19349458;
                        double dist = 0.0;
                        int findServerIndex = 0;
                        for (int index : mapKey.keySet()) {
                            if (tempBlackList.contains(mapValue.get(index).get(5))) {
                                continue;
                            }

                            Location source = new Location("Source");
                            source.setLatitude(selfLat);
                            source.setLongitude(selfLon);

                            List<String> ls = mapValue.get(index);
                            Location dest = new Location("Dest");
                            dest.setLatitude(Double.parseDouble(ls.get(0)));
                            dest.setLongitude(Double.parseDouble(ls.get(1)));

                            double distance = source.distanceTo(dest);
                            if (tmp > distance) {
                                tmp = distance;
                                dist = distance;
                                findServerIndex = index;
                            }
                        }
                        String uploadAddr = mapKey.get(findServerIndex);
                        final List<String> info = mapValue.get(findServerIndex);
                        final double distance = dist;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startButton.setTextSize(13);
                                startButton.setText(String.format("Hosted by %s (%s) [%s km]", info.get(5), info.get(3), new DecimalFormat("#.##").format(distance / 1000)));
                            }
                        });


                        final LinearLayout chartPing = (LinearLayout) findViewById(R.id.chartPing);
                        XYSeriesRenderer pingRenderer = new XYSeriesRenderer();
                        XYSeriesRenderer.FillOutsideLine pingFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                        pingFill.setColor(Color.parseColor("#4d5a6a"));
                        pingRenderer.addFillOutsideLine(pingFill);
                        pingRenderer.setDisplayChartValues(false);
                        pingRenderer.setShowLegendItem(false);
                        pingRenderer.setColor(Color.parseColor("#4d5a6a"));
                        pingRenderer.setLineWidth(5);
                        final XYMultipleSeriesRenderer multiPingRenderer = new XYMultipleSeriesRenderer();
                        multiPingRenderer.setXLabels(0);
                        multiPingRenderer.setYLabels(0);
                        multiPingRenderer.setZoomEnabled(false);
                        multiPingRenderer.setXAxisColor(Color.parseColor("#647488"));
                        multiPingRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                        multiPingRenderer.setPanEnabled(true, true);
                        multiPingRenderer.setZoomButtonsVisible(false);
                        multiPingRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiPingRenderer.addSeriesRenderer(pingRenderer);


                        final LinearLayout chartDownload = (LinearLayout) findViewById(R.id.chartDownload);
                        XYSeriesRenderer downloadRenderer = new XYSeriesRenderer();
                        XYSeriesRenderer.FillOutsideLine downloadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                        downloadFill.setColor(Color.parseColor("#4d5a6a"));
                        downloadRenderer.addFillOutsideLine(downloadFill);
                        downloadRenderer.setDisplayChartValues(false);
                        downloadRenderer.setColor(Color.parseColor("#4d5a6a"));
                        downloadRenderer.setShowLegendItem(false);
                        downloadRenderer.setLineWidth(5);
                        final XYMultipleSeriesRenderer multiDownloadRenderer = new XYMultipleSeriesRenderer();
                        multiDownloadRenderer.setXLabels(0);
                        multiDownloadRenderer.setYLabels(0);
                        multiDownloadRenderer.setZoomEnabled(false);
                        multiDownloadRenderer.setXAxisColor(Color.parseColor("#647488"));
                        multiDownloadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                        multiDownloadRenderer.setPanEnabled(false, false);
                        multiDownloadRenderer.setZoomButtonsVisible(false);
                        multiDownloadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiDownloadRenderer.addSeriesRenderer(downloadRenderer);


                        final LinearLayout chartUpload = (LinearLayout) findViewById(R.id.chartUpload);
                        XYSeriesRenderer uploadRenderer = new XYSeriesRenderer();
                        XYSeriesRenderer.FillOutsideLine uploadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                        uploadFill.setColor(Color.parseColor("#4d5a6a"));
                        uploadRenderer.addFillOutsideLine(uploadFill);
                        uploadRenderer.setDisplayChartValues(false);
                        uploadRenderer.setColor(Color.parseColor("#4d5a6a"));
                        uploadRenderer.setShowLegendItem(false);
                        uploadRenderer.setLineWidth(5);
                        final XYMultipleSeriesRenderer multiUploadRenderer = new XYMultipleSeriesRenderer();
                        multiUploadRenderer.setXLabels(0);
                        multiUploadRenderer.setYLabels(0);
                        multiUploadRenderer.setZoomEnabled(false);
                        multiUploadRenderer.setXAxisColor(Color.parseColor("#647488"));
                        multiUploadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                        multiUploadRenderer.setPanEnabled(false, false);
                        multiUploadRenderer.setZoomButtonsVisible(false);
                        multiUploadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiUploadRenderer.addSeriesRenderer(uploadRenderer);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pingTextView.setText("0 ms");
                                chartPing.removeAllViews();
                                TextView centree = (TextView) findViewById(R.id.centree);
                                centree.setText("0");

                                downloadTextView.setText("0 Mbps");
                                chartDownload.removeAllViews();

                                TextView centreer = (TextView) findViewById(R.id.centree);
                                centreer.setText("0");
                                uploadTextView.setText("0 Mbps");
                                chartUpload.removeAllViews();
                            }
                        });
                        final List<Double> pingRateList = new ArrayList<>();
                        final List<Double> downloadRateList = new ArrayList<>();
                        final List<Double> uploadRateList = new ArrayList<>();
                        Boolean pingTestStarted = false;
                        Boolean pingTestFinished = false;
                        Boolean downloadTestStarted = false;
                        Boolean downloadTestFinished = false;
                        Boolean uploadTestStarted = false;
                        Boolean uploadTestFinished = false;


                        final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 6);
                        final HttpDownloadTest downloadTest = new HttpDownloadTest(uploadAddr.replace(uploadAddr.split("/")[uploadAddr.split("/").length - 1], ""));
                        final HttpUploadTest uploadTest = new HttpUploadTest(uploadAddr);



                        while (true) {
                            if (!pingTestStarted) {
                                pingTest.start();
                                pingTestStarted = true;
                            }
                            if (pingTestFinished && !downloadTestStarted) {
                                downloadTest.start();
                                downloadTestStarted = true;
                            }
                            if (downloadTestFinished && !uploadTestStarted) {
                                uploadTest.start();
                                uploadTestStarted = true;
                            }



                            if (pingTestFinished) {

                                if (pingTest.getAvgRtt() == 0) {
                                    System.out.println("Ping error...");
                                } else {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pingTextView.setText(dec.format(pingTest.getAvgRtt()) + " ms");
                                        }
                                    });
                                }
                            } else {
                                pingRateList.add(pingTest.getInstantRtt());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pingTextView.setText(dec.format(pingTest.getInstantRtt()) + " ms");
                                    }
                                });


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        XYSeries pingSeries = new XYSeries("");
                                        pingSeries.setTitle("");

                                        int count = 0;
                                        List<Double> tmpLs = new ArrayList<>(pingRateList);
                                        for (Double val : tmpLs) {
                                            pingSeries.add(count++, val);
                                        }

                                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                        dataset.addSeries(pingSeries);

                                        GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiPingRenderer);
                                        chartPing.addView(chartView, 0);

                                    }
                                });
                            }



                            if (pingTestFinished) {
                                if (downloadTestFinished) {

                                    if (downloadTest.getFinalDownloadRate() == 0) {
                                        System.out.println("Download error...");
                                    } else {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                TextView centree = (TextView) findViewById(R.id.centree);
                                                centree.setText(dec.format(downloadTest.getFinalDownloadRate()));

                                                downloadTextView.setText(dec.format(downloadTest.getFinalDownloadRate()) + " Mbps");
                                            }
                                        });
                                    }
                                } else {

                                    double downloadRate = downloadTest.getInstantDownloadRate();
                                    downloadRateList.add(downloadRate);
                                    position = getPositionByRate(downloadRate);

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                            rotate.setInterpolator(new LinearInterpolator());
                                            rotate.setDuration(100);
                                            barImageView.startAnimation(rotate);


                                            TextView centree = (TextView) findViewById(R.id.centree);
                                            centree.setText(dec.format(downloadTest.getInstantDownloadRate()));

                                            downloadTextView.setText(dec.format(downloadTest.getInstantDownloadRate()) + " Mbps");

                                        }

                                    });
                                    lastPosition = position;


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            XYSeries downloadSeries = new XYSeries("");
                                            downloadSeries.setTitle("");

                                            List<Double> tmpLs = new ArrayList<>(downloadRateList);
                                            int count = 0;
                                            for (Double val : tmpLs) {
                                                downloadSeries.add(count++, val);
                                            }

                                            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                            dataset.addSeries(downloadSeries);

                                            GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiDownloadRenderer);
                                            chartDownload.addView(chartView, 0);
                                        }
                                    });

                                }
                            }



                            if (downloadTestFinished) {
                                if (uploadTestFinished) {

                                    if (uploadTest.getFinalUploadRate() == 0) {
                                        System.out.println("Upload error...");
                                    } else {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                TextView centree = (TextView) findViewById(R.id.centree);
                                                centree.setText(dec.format(uploadTest.getFinalUploadRate()));
                                                uploadTextView.setText(dec.format(uploadTest.getFinalUploadRate()) + " Mbps");
                                            }
                                        });
                                    }
                                } else {

                                    double uploadRate = uploadTest.getInstantUploadRate();
                                    uploadRateList.add(uploadRate);
                                    position = getPositionByRate(uploadRate);

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                            rotate.setInterpolator(new LinearInterpolator());
                                            rotate.setDuration(100);
                                            barImageView.startAnimation(rotate);

                                            TextView centree = (TextView) findViewById(R.id.centree);
                                            centree.setText(dec.format(uploadTest.getInstantUploadRate()));


                                            uploadTextView.setText(dec.format(uploadTest.getInstantUploadRate()) + " Mbps");
                                        }

                                    });
                                    lastPosition = position;


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            XYSeries uploadSeries = new XYSeries("");
                                            uploadSeries.setTitle("");

                                            int count = 0;
                                            List<Double> tmpLs = new ArrayList<>(uploadRateList);
                                            for (Double val : tmpLs) {
                                                if (count == 0) {
                                                    val = 0.0;
                                                }
                                                uploadSeries.add(count++, val);
                                            }

                                            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                            dataset.addSeries(uploadSeries);

                                            GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiUploadRenderer);
                                            chartUpload.addView(chartView, 0);
                                        }
                                    });

                                }
                            }


                            if (pingTestFinished && downloadTestFinished && uploadTest.isFinished()) {
                                break;
                            }

                            if (pingTest.isFinished()) {
                                pingTestFinished = true;
                            }
                            if (downloadTest.isFinished()) {
                                downloadTestFinished = true;
                            }
                            if (uploadTest.isFinished()) {
                                uploadTestFinished = true;
                            }

                            if (pingTestStarted && !pingTestFinished) {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                }
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                }
                            }
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startButton.setEnabled(true);
                                startButton.setTextSize(16);
                                procheck2();
                                startButton.setText("Restart Test");
                            }
                        });


                    }
                }).start();
            }
        });
    }


    public int getPositionByRate(double rate) {
        if (rate <= 1) {
            return (int) (rate * 30);

        } else if (rate <= 10) {
            return (int) (rate * 6) + 30;

        } else if (rate <= 30) {
            return (int) ((rate - 10) * 3) + 90;

        } else if (rate <= 50) {
            return (int) ((rate - 30) * 1.5) + 150;

        } else if (rate <= 100) {
            return (int) ((rate - 50) * 1.2) + 180;
        }

        return 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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


    public void procheck2()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean(UpgradePro, false);

        if(strPref)
        {



        }
        else {
//            final InterstitialAd mInterstitial = new InterstitialAd(this);
//            mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
//            mInterstitial.loadAd(new AdRequest.Builder().build());
//            mInterstitial.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    // TODO Auto-generated method stub
//                    super.onAdLoaded();
//                    if (mInterstitial.isLoaded()) {
//                        mInterstitial.show();
//                    }
//                }
//            });



        }

    }
}
