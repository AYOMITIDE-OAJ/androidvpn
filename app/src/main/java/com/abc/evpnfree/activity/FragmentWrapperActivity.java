package com.abc.evpnfree.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.abc.evpnfree.Fragments.PhoneBooster_Fragment;
import com.abc.evpnfree.R;

public class FragmentWrapperActivity extends AppCompatActivity {

    public final static String REQUEST_ACTIVITY_CODE = "CODE_ACTIVITY";
    public final static String RUNTIME_MODE = "RUNTIME_MODE";

    public final static String
            BOOSTER_CODE = "PHONEBOOSTER",

            BATTERY_SAVER_CODE = "BATTERYSAVER",
            NOTIFICATIONS_CLEANER_CODE = "NOTIFICATIONSCLEANER";

    private String requestStateCode;

    public static boolean notWaitJustRedirect = false;
    private boolean isExitingFromApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_wrapper);

        try {
            Intent intent = getIntent ();

            requestStateCode = intent.getStringExtra(REQUEST_ACTIVITY_CODE);
        } catch (Exception e) {

        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (requestStateCode) {

            case BOOSTER_CODE:
                PhoneBooster_Fragment speedBooster_fragment = new PhoneBooster_Fragment();
                ft.replace(R.id.wrapper, speedBooster_fragment);
                ft.commit();
                break;


            default:
                Toast.makeText(this, "Wrong app query, try now!", Toast.LENGTH_SHORT).show();
                finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isExitingFromApp) {
            startActivity (new Intent (getApplicationContext (), MainActivity.class));

            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (requestStateCode.equals(NOTIFICATIONS_CLEANER_CODE)) {
                    notWaitJustRedirect = true;
                    redirectToSecureSettings();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public void redirectToSecureSettings() {
        Toast.makeText(getApplicationContext(), "You should allow this app to use notification control before using",
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:


                finish ();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {





            Intent startHomescreen = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(startHomescreen);

            finish();

            return true;
        } else {
            return super.onKeyDown(pKeyCode, pEvent);
        }
    }
}
