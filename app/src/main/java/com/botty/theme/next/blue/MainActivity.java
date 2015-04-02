package com.botty.theme.next.blue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.botty.theme.next.blue.Activities.Activity_About;
import com.botty.theme.next.blue.Activities.Donate;
import com.botty.theme.next.blue.Fragment.Fragment_Swiper;
import com.botty.theme.next.blue.Fragment.Kill_notSupport;
import com.botty.theme.next.blue.Fragment.ThemeInst;
import com.botty.theme.next.blue.Fragment.kill;
import com.botty.theme.next.blue.Util.ConnectionDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.koushikdutta.ion.Ion;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.IOException;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    // Connection detector
    ConnectionDetector cd;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "71346899480";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    static public String PackageThemeCM = "org.cyanogenmod.themes.provider";
    GoogleCloudMessaging gcm;
    Context context;

    String regid;
    FrameLayout frameLayout;

    private Drawer.Result drawerMaterial = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isPackageExisted(PackageThemeCM)){
            ThemeSupported();
        }else {
            ThemeNotSupported();
        }

        ImageView back = (ImageView) findViewById(R.id.imgBack);
        Ion.with(back).load("https://lh4.googleusercontent.com/-L8u7iZ3cdgI/VPCGCbV2m8I/AAAAAAAABxA/nnIoWqLbt5k/s1024-no/web_hi_res_512.pngx");

        context = getApplicationContext();
        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Snackbar.with(getApplicationContext()) // context
                    .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                    .text("Internet Connection Error,\n" +
                            "Please connect to working Internet connection")
                    .actionLabel("Undo") // action button label
                    .actionColor(Color.YELLOW) // action button label color
                    .actionListener(new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            Log.d(TAG, "Undoing something");
                        }
                    }) // action button's ActionClickListener
                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                    .show(this); // activity where it is displayed
            // stop executing code by return
            return;
        }
        // Check device for Play Services APK.
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        }

    public void ThemeSupported(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            kill fragment = new kill();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }else {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ThemeInst fragment = new ThemeInst();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

        drawerMaterial = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.my_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.theme_item_drawer)).withIcon(FontAwesome.Icon.faw_download),
                        new PrimaryDrawerItem().withName(getString(R.string.icons_item_drawer)).withIcon(FontAwesome.Icon.faw_circle_o),
                        new PrimaryDrawerItem().withName(getString(R.string.donate_item_drawer)).withIcon(FontAwesome.Icon.faw_money),
                        new PrimaryDrawerItem().withName(getString(R.string.wallpapers_item_drawer)).withIcon(FontAwesome.Icon.faw_picture_o).withBadge("◥"),
                        new SectionDrawerItem().withName(getString(R.string.some_stuff_item_drawer)),
                        new SecondaryDrawerItem().withName(getString(R.string.info_item_drawer)).withIcon(FontAwesome.Icon.faw_info)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (position == 0) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            ThemeInst fragment = new ThemeInst();
                            fragmentTransaction.replace(R.id.content_frame, fragment);
                            fragmentTransaction.commit();
                        } else if (position == 1) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment_Swiper fragment = new Fragment_Swiper();
                            fragmentTransaction.replace(R.id.content_frame, fragment);
                            fragmentTransaction.commit();
                        } else if (position == 2) {
                            Intent intent = new Intent(getApplicationContext(), Donate.class);
                            startActivity(intent);
                        } else if (position == 3) {
                            Intent i;
                            PackageManager manager = getPackageManager();
                            try {
                                i = manager.getLaunchIntentForPackage("com.botty.wall");
                                if (i == null)
                                    throw new PackageManager.NameNotFoundException();
                                i.addCategory(Intent.CATEGORY_LAUNCHER);
                                startActivity(i);
                            } catch (PackageManager.NameNotFoundException e) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.botty.wall"));
                                startActivity(intent);
                            }
                        } else if (position == 5) {
                            Intent intent = new Intent(getApplicationContext(), Activity_About.class);
                            startActivity(intent);
                        }
                    }
                })
                .withSelectedItem(0)
                .build();
    }

    public void ThemeNotSupported(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Kill_notSupport fragment = new Kill_notSupport();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }else {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment_Swiper fragment = new Fragment_Swiper();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

        drawerMaterial = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.my_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.icons_item_drawer)).withIcon(FontAwesome.Icon.faw_circle_o),
                        new PrimaryDrawerItem().withName(getString(R.string.donate_item_drawer)).withIcon(FontAwesome.Icon.faw_money),
                        new PrimaryDrawerItem().withName(getString(R.string.wallpapers_item_drawer)).withIcon(FontAwesome.Icon.faw_picture_o).withBadge("◥"),
                        new SectionDrawerItem().withName(getString(R.string.some_stuff_item_drawer)),
                        new SecondaryDrawerItem().withName(getString(R.string.info_item_drawer)).withIcon(FontAwesome.Icon.faw_info)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (position == 0) {
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment_Swiper fragment = new Fragment_Swiper();
                            fragmentTransaction.replace(R.id.content_frame, fragment);
                            fragmentTransaction.commit();
                        } else if (position == 1) {
                            Intent intent = new Intent(getApplicationContext(), Donate.class);
                            startActivity(intent);
                        } else if (position == 2) {
                            Intent i;
                            PackageManager manager = getPackageManager();
                            try {
                                i = manager.getLaunchIntentForPackage("com.botty.wall");
                                if (i == null)
                                    throw new PackageManager.NameNotFoundException();
                                i.addCategory(Intent.CATEGORY_LAUNCHER);
                                startActivity(i);
                            } catch (PackageManager.NameNotFoundException e) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.botty.wall"));
                                startActivity(intent);
                            }
                        } else if (position == 4) {
                            Intent intent = new Intent(getApplicationContext(), Activity_About.class);
                            startActivity(intent);
                        }
                    }
                })
                .withSelectedItem(0)
                .build();
    }

    public boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage)) return true;
        }
        return false;
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "You are welcome !";

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}