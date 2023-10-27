package com.pay.treminalauto;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.pay.treminalauto.network.ApiClient;
import com.pay.treminalauto.network.TokenProvider;
import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.TerminalApplicationDelegate;
import com.stripe.stripeterminal.external.callable.Callback;
import com.stripe.stripeterminal.external.callable.Cancelable;
import com.stripe.stripeterminal.external.callable.DiscoveryListener;
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback;
import com.stripe.stripeterminal.external.callable.ReaderCallback;
import com.stripe.stripeterminal.external.models.CardPresentParameters;
import com.stripe.stripeterminal.external.models.CollectConfiguration;
import com.stripe.stripeterminal.external.models.ConnectionConfiguration;
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration;
import com.stripe.stripeterminal.external.models.DiscoveryMethod;
import com.stripe.stripeterminal.external.models.PaymentIntent;
import com.stripe.stripeterminal.external.models.PaymentIntentParameters;
import com.stripe.stripeterminal.external.models.PaymentMethodOptionsParameters;
import com.stripe.stripeterminal.external.models.Reader;
import com.stripe.stripeterminal.external.models.TerminalException;
import com.stripe.stripeterminal.external.models.TippingConfiguration;
import com.stripe.stripeterminal.log.LogLevel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements DiscoveryListener {
    @NotNull
    private static final String AMOUNT =
            "com.stripe.example.fragment.event.EventFragment.amount";
    @NotNull
    private static final String CURRENCY =
            "com.stripe.example.fragment.event.EventFragment.currency";
    @NotNull
    private static final String REQUEST_PAYMENT =
            "com.stripe.example.fragment.event.EventFragment.request_payment";
    @NotNull
    private static final String READ_REUSABLE_CARD =
            "com.stripe.example.fragment.event.EventFragment.read_reusable_card";
    @NotNull
    private static final String SKIP_TIPPING =
            "com.stripe.example.fragment.event.EventFragment.skip_tipping";
    @NotNull
    private static final String EXTENDED_AUTH =
            "com.stripe.example.fragment.event.EventFragment.incremental_auth";
    @NotNull
    private static final String INCREMENTAL_AUTH =
            "com.stripe.example.fragment.event.EventFragment.extended_auth";

    private static final boolean DO_NOT_ENABLE_MOTO = false;
    private boolean ISPAYMENTDONE = false;
    public static final String TAG = "MainActivity";
    final Callback discoveryCallback = new Callback() {
        @Override
        public void onSuccess() {
            Log.e(TAG, "onSuccess:------- ");
        }

        @Override
        public void onFailure(@NotNull TerminalException e) {
            Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
            Log.e(TAG, "onFailure: " + e.getMessage());
            Log.e(TAG, "onFailure: " + e.getCause());
        }
    };
    public Cancelable discoveryTask;
    @Nullable
    public Cancelable collectTask;

    public List<Reader> readerList = new ArrayList<>();
    DiscoveryConfiguration config;
    private PaymentIntent paymentIntent;
    @NotNull
    private final PaymentIntentCallback processPaymentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NotNull PaymentIntent paymentIntent) {
            try {


              ResponseBody v =  ApiClient.capturePaymentIntent(paymentIntent.getId());
                Log.e(TAG, "onSuccess: "+v.byteString() );
                Log.e(TAG, "onSuccess: "+v.byteString() );
                //PlaceOrderApi();
              //  completeFlow();
            } catch (IOException e) {
                Log.e("StripeExample", e.getMessage(), e);
               // completeFlow();
            }
        }

        @Override
        public void onFailure(@NotNull TerminalException e) {
            Log.e(TAG, "onFailure: "+e.getLocalizedMessage() );
        }
    };

    @NotNull
    private final PaymentIntentCallback cancelPaymentIntentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NotNull PaymentIntent paymentIntent) {

        }

        @Override
        public void onFailure(@NotNull TerminalException e) {
            Log.e(TAG, "onFailure: "+e.getLocalizedMessage() );
        }
    };

    @NotNull
    private final PaymentIntentCallback collectPaymentMethodCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NotNull PaymentIntent paymentIntent) {

            Terminal.getInstance().processPayment(paymentIntent, processPaymentCallback);
        }

        @Override
        public void onFailure(@NotNull TerminalException e) {
            Log.e(TAG, "onFailure: "+e.getLocalizedMessage() );

        }
    };

    @NotNull
    private final PaymentIntentCallback createPaymentIntentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NotNull PaymentIntent intent) {
            paymentIntent = intent;
            final Bundle bundle = new Bundle();
            bundle.putLong(AMOUNT, 100);
            bundle.putString(CURRENCY, "usd");
            bundle.putBoolean(REQUEST_PAYMENT, true);
            bundle.putBoolean(READ_REUSABLE_CARD, false);
            bundle.putBoolean(SKIP_TIPPING, false);
            bundle.putBoolean(EXTENDED_AUTH, false);
            bundle.putBoolean(INCREMENTAL_AUTH, false);
            final CollectConfiguration collectConfig = new CollectConfiguration.Builder()
                    .skipTipping(false)
                    .setMoto(DO_NOT_ENABLE_MOTO)
                    .setTippingConfiguration(
                            new TippingConfiguration.Builder().build()
                    ).build();
            collectTask = Terminal.getInstance().collectPaymentMethod(
                    paymentIntent, collectPaymentMethodCallback, collectConfig);
        }

        @Override
        public void onFailure(@NotNull TerminalException e) {
            Log.e(TAG, "onFailure: "+e.getLocalizedMessage());;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            TerminalApplicationDelegate.onCreate(getApplication());
        } catch (Exception e) {
            Log.e("TAG", "StripeTerminalApplication onCreate: " + e.getMessage());
            Log.e("TAG", "StripeTerminalApplication onCreate: " + e.getLocalizedMessage());
            Log.e("TAG", "StripeTerminalApplication onCreate: " + e.getCause());
        }

        if (!Terminal.isInitialized()) {

            Log.e("TAG", "requestPermissionsIfNecessarySdkBelow31:initialize ");
            try {
                Terminal.initTerminal(MainActivity.this, LogLevel.NONE, new TokenProvider(),
                        new TerminalEventListener());
                config = new DiscoveryConfiguration(
                        0, DiscoveryMethod.INTERNET
                        , true);
                if (Terminal.getInstance().getConnectedReader() == null) {
                    discoveryTask =
                            Terminal
                                    .getInstance()
                                    .discoverReaders(config, this, discoveryCallback);
                }
            } catch (TerminalException e) {
                Log.e("TAG", "initialize: " + e.getMessage());
                Log.e("TAG", "initialize: " + e.getLocalizedMessage());
                Log.e("TAG", "initialize: " + e.getCause());
            }
            ;
        }

    }

    @Override
    public void onUpdateDiscoveredReaders(@NonNull List<Reader> list) {
        this.readerList = list;
        if (readerList.size() > 0) {
            Terminal.getInstance().connectInternetReader(
                    readerList.get(0),
                    new ConnectionConfiguration.InternetConnectionConfiguration(),
                    new ReaderCallback() {
                        @Override
                        public void onSuccess(@NonNull Reader reader) {
                            Log.e(TAG, "onSuccess: "+reader.getIpAddress() );
                            Log.e(TAG, "onSuccess: "+reader.getId() );
                            CardPresentParameters.Builder cardPresentParametersBuilder = new CardPresentParameters.Builder();
                            PaymentMethodOptionsParameters paymentMethodOptionsParameters = new PaymentMethodOptionsParameters.Builder()
                                    .setCardPresentParameters(cardPresentParametersBuilder.build())
                                    .build();
                            final PaymentIntentParameters params = new PaymentIntentParameters.Builder()
                                    .setAmount(100)
                                    .setCurrency("usd")
                                    .setPaymentMethodOptionsParameters(paymentMethodOptionsParameters)
                                    .build();
                            Terminal.getInstance().createPaymentIntent(params, createPaymentIntentCallback);
                        }
                        @Override
                        public void onFailure(@NonNull TerminalException e) {
                            Log.e(TAG, "onFailure: "+e.getCause() );
                            Log.e(TAG, "onFailure: "+e.getMessage() );
                            Log.e(TAG, "onFailure: "+e.getLocalizedMessage() );
                        }
                    }
            );
        }
    }
}