package com.botty.theme.next.blue.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.botty.theme.next.blue.R;
import com.botty.theme.next.blue.Util.ConnectionDetector;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import static com.botty.theme.next.blue.R.color.*;

/**
 * Created by BottyIvan on 31/03/15.
 */
public class Donate extends ActionBarActivity implements BillingProcessor.IBillingHandler {

    // Connection detector
    ConnectionDetector cd;
    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    Context context;

    BillingProcessor bp;
    FloatingActionButton fab_donate;
    private boolean readyToPurchase = false;
    private static final String LOG_TAG = "";
    public static final String PRODUCT_ID_0 = "some_love";
    public static final String PRODUCT_ID_1 = "love";
    public static final String PRODUCT_ID_2 = "pure_love";
    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA274wObbop79owruTuMGezSuODLIs0779EsEDgP/DIqBdPiXBQHlXLCjyo4x96cg7DqxEEw3cPEi4GzlwaCDwY7Ei3uxXHC95wCWVMwT2c+oQ9Jk7v8fT2VZ4jlUO3fRpaNJikeH5B4fmcwv6ZNklVElK4rUFiadvKHkNLCfaDhNGuR1wCx3HFSJDUtfP74d96JcSk+6iZQLS2LvKIMyxiC5BOMVQ4WhtEkN4yhPqaS5WlME9XvzAT1LQzrubWOfWgS/bLBV5SqoiH2V4Mf3oRicoiSgNCna48OvKgyGSWe37i+GbGKRz+hmLXJwCRep9zUatiz1RicgK9u969MY8vwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_trasp);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        GetInternetConnection();
        bp = new BillingProcessor(this, LICENSE_KEY, this);

        fab_donate = (FloatingActionButton) findViewById(R.id.donate_btn);
        YoYo.with(Techniques.FadeInUp)
                .playOn(fab_donate);
        fab_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialogChoice();
            }
        });
    }

    public void MaterialDialogChoice(){
        new MaterialDialog.Builder(this)
                .title(R.string.title_material_dialog_donate)
                .items(R.array.items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            bp.purchase(Donate.this, PRODUCT_ID_0);
                            bp.consumePurchase(PRODUCT_ID_0);
                        } else if (which == 1) {
                            bp.purchase(Donate.this, PRODUCT_ID_1);
                            bp.consumePurchase(PRODUCT_ID_1);
                        } else if (which == 2) {
                            bp.purchase(Donate.this, PRODUCT_ID_2);
                            bp.consumePurchase(PRODUCT_ID_2);
                        }
                    }
                })
                .positiveText(android.R.string.cancel)
                .show();
    }

    public void GetInternetConnection(){
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }
}