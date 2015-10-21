package com.botty.theme.next.blue;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.botty.theme.next.blue.Activities.Activity_About;
import com.botty.theme.next.blue.Activities.Donate;
import com.botty.theme.next.blue.Activities.MyIntro;
import com.botty.theme.next.blue.Fragment.Fragment_Swiper;
import com.botty.theme.next.blue.Fragment.ThemeInst;
import com.botty.theme.next.blue.Util.ConnectionDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    Toolbar toolbar;
    ImageView backwall;
    FrameLayout frameLayout,back;

    private Drawer result = null;

    /*
     * Ask permission
     */
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private PackageManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            iCanM();
            UIforM();
        }else{
            UIforM();
        }

        context = getApplicationContext();
        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Snackbar.with(getApplicationContext()) // context
                    .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                    .text("Internet Connection Error,\n" +
                            getString(R.string.snackbar_internet_req))
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

    public void UIforM(){
        if (isPackageExisted(PackageThemeCM)) {
            ThemeSupported();
        } else {
            ThemeNotSupported();
        }
    }
    private void iCanM(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read/Write Storage");
        if (!addPermission(permissionsList, Manifest.permission.GET_ACCOUNTS))
            permissionsNeeded.add("Get Account");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to the permission for use the app !!";
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }}

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    UIforM();
                } else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        UIforM();
                    }else {
                        // Permission Denied
                        String msg = "You need to grant access to Write/read storage for download the theme !";
                       showMessageOKCancel(msg, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent i = manager.getLaunchIntentForPackage("com.android.settings");
                               MainActivity.this.startActivity(i);
                           }
                       });
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public void ThemeSupported() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            Intent intent = new Intent(this, MyIntro.class);
            startActivity(intent);
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

        // Handle Toolbar
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.my_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.theme_item_drawer)).withIcon(FontAwesome.Icon.faw_download),
                        new PrimaryDrawerItem().withName(getString(R.string.icons_item_drawer)).withIcon(FontAwesome.Icon.faw_circle_o),
                        new PrimaryDrawerItem().withName(getString(R.string.donate_item_drawer)).withIcon(FontAwesome.Icon.faw_dot_circle_o),
                        new PrimaryDrawerItem().withName(getString(R.string.wallpapers_item_drawer)).withIcon(FontAwesome.Icon.faw_picture_o),
                        new SectionDrawerItem().withName(getString(R.string.some_stuff_item_drawer)),
                        new SecondaryDrawerItem().withName(getString(R.string.info_item_drawer)).withIcon(FontAwesome.Icon.faw_info),
                        new SecondaryDrawerItem().withName("OpenStuff").withIcon(FontAwesome.Icon.faw_github)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem iDrawerItem) {
                        if (position == 1) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            ThemeInst fragment = new ThemeInst();
                            fragmentTransaction.replace(R.id.content_frame, fragment);
                            fragmentTransaction.commit();
                        } else if (position == 2) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment_Swiper fragment = new Fragment_Swiper();
                            fragmentTransaction.replace(R.id.content_frame, fragment);
                            fragmentTransaction.commit();
                        } else if (position == 3) {
                            Intent intent = new Intent(getApplicationContext(), Donate.class);
                            startActivity(intent);
                        } else if (position == 4) {
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
                        } else if (position == 6) {
                            Intent intent = new Intent(getApplicationContext(), Activity_About.class);
                            startActivity(intent);
                        } else if (position == 7) {
                            LibsFragment fragment = new LibsBuilder()
                                    .withLibraries()
                                    .withAboutAppName(getString(R.string.app_name))
                                    .withAboutDescription(getString(R.string.description_in_about_library))
                                    .withAboutIconShown(true)
                                    .withVersionShown(true)
                                    .withLicenseShown(true)
                                    .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                                    .fragment();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(1)
                .build();
    }

    public void ThemeNotSupported(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            Intent intent = new Intent(this, MyIntro.class);
            startActivity(intent);
            /*android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            kill fragment = new kill();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();*/
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
        // Handle Toolbar
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.my_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.icons_item_drawer)).withIcon(FontAwesome.Icon.faw_circle_o),
                        new PrimaryDrawerItem().withName(getString(R.string.donate_item_drawer)).withIcon(FontAwesome.Icon.faw_dot_circle_o),
                        new PrimaryDrawerItem().withName(getString(R.string.wallpapers_item_drawer)).withIcon(FontAwesome.Icon.faw_picture_o),
                        new SectionDrawerItem().withName(getString(R.string.some_stuff_item_drawer)),
                        new SecondaryDrawerItem().withName(getString(R.string.info_item_drawer)).withIcon(FontAwesome.Icon.faw_info),
                        new SecondaryDrawerItem().withName("OpenStuff").withIcon(FontAwesome.Icon.faw_github)
        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem iDrawerItem) {
                        if (position == 1) {
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
                        } else if (position == 6) {
                            LibsFragment fragment = new LibsBuilder()
                                    .withLibraries()
                                    .withAboutAppName(getString(R.string.app_name))
                                    .withAboutDescription(getString(R.string.description_in_about_library))
                                    .withAboutIconShown(true)
                                    .withVersionShown(true)
                                    .withLicenseShown(true)
                                    .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                                    .fragment();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(1)
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
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_my_intro, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.easter){
            //Do whatever you want to do
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://www.suicidesquad.com/"));
            startActivity(i);
            return true;
        } else if (id == R.id.play){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://search?q=pub:Ivan Botty"));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}