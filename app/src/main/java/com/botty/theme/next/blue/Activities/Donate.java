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
    private static final String PRODUCT_ID = "";
    private static final String LICENSE_KEY = "YOUR LICENSE KEY FOR THIS APPLICATION";

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

        fab_donate = (FloatingActionButton) findViewById(R.id.donate_btn);
        YoYo.with(Techniques.FadeInUp)
                .playOn(fab_donate);
        fab_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bp.purchase(PRODUCT_ID);
                // bp.consumePurchase(PRODUCT_ID);
            }
        });
        bp = new BillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
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