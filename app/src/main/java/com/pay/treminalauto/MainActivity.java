package com.pay.treminalauto;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity extends AppCompatActivity implements DiscoveryListener , ReaderssAdapter.OnClicked {

    public static final String TAG = "MainActivity";
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
    @NotNull
    private final PaymentIntentCallback cancelPaymentIntentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NotNull PaymentIntent paymentIntent) {

        }

        @Override
        public void onFailure(@NotNull TerminalException e) {
            Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
        }
    };
    public Cancelable discoveryTask;
    @Nullable
    public Cancelable collectTask;
    public List<Reader> readerList = new ArrayList<>();
    MutableLiveData<String> status = new MutableLiveData<>("Loading...");
    @NotNull
    private final PaymentIntentCallback processPaymentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NotNull PaymentIntent paymentIntent) {
            try {

                runOnUiThread(() -> status.setValue("1$  Recieved"));
                ResponseBody v = ApiClient.capturePaymentIntent(paymentIntent.getId());
                Log.e(TAG, "onSuccess: " + v.byteString());
                Log.e(TAG, "onSuccess: " + v.byteString());
                PlaceOrderApi();
                //  completeFlow();
            } catch (IOException e) {
                Log.e("StripeExample", e.getMessage(), e);
                // completeFlow();
            }
        }

        @Override
        public void onFailure(@NotNull TerminalException e) {
            Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
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
            Log.e(TAG, "onFailure: " + e.getLocalizedMessage());

        }
    };
    DiscoveryConfiguration config;
    private boolean ISPAYMENTDONE = false;
    private TextView text_view;
    private PaymentIntent paymentIntent;
    private Dialog dialogq;
    @NotNull
    private final PaymentIntentCallback createPaymentIntentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NotNull PaymentIntent intent) {
            paymentIntent = intent;
            runOnUiThread(() -> status.setValue(" Getting Payment of  1$"));
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
            Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
            ;
        }
    };

    private void PlaceOrderApi() {

        Log.e(TAG, "PlaceOrderApi:  Payment Done   ");
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Payment Done", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_view = findViewById(R.id.text_view);

        status.observe(this, s -> {
            runOnUiThread(() -> text_view.setText(s));
        });
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
                Terminal.initTerminal(MainActivity.this, LogLevel.VERBOSE, new TokenProvider(),
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
                runOnUiThread(() -> status.setValue("Getting ready ..."));

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
        if (readerList.size() >0) {

            discoveryTask.cancel(discoveryCallback);
            runOnUiThread(() -> {
                Log.e(TAG, "onUpdateDiscoveredReaders:--- " + readerList.size());
                 for (Reader reader :readerList){
                     Log.e(TAG, "onUpdateDiscoveredReaders:--- " + reader.getLabel());
                 }
                 dialogq = new Dialog(MainActivity.this);
                dialogq.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogq.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
                dialogq.setContentView(R.layout.reders_list_dialog);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialogq.getWindow();
                lp.copyFrom(window.getAttributes());
                Button ivCancel = dialogq.findViewById(R.id.btncncel);
                RecyclerView recycle = dialogq.findViewById(R.id.recycle);
               ivCancel.setOnClickListener(D -> {
                    dialogq.dismiss();
                });
                ReaderssAdapter  adapter = new ReaderssAdapter(MainActivity.this,this::onItemClicked);
                recycle.setAdapter(adapter);
                adapter.updateEvents(readerList);
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialogq.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogq.show();

               // status.setValue("connect to Reader ");
            });

        } else {



            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "No reader Available", Toast.LENGTH_SHORT).show();



            });

        }
    }


    @Override
    public void onItemClicked(Reader id) {
        dialogq.dismiss();
        Terminal.getInstance().connectInternetReader(
               id,
                new ConnectionConfiguration.InternetConnectionConfiguration(),
                new ReaderCallback() {
                    @Override
                    public void onSuccess(@NonNull Reader reader) {
                        Log.e(TAG, "onSuccess: " + reader.getIpAddress());
                        Log.e(TAG, "onSuccess: " + reader.getId());
                        runOnUiThread(() -> status.setValue("connected"));
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
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                        Log.e(TAG, "onFailure: " + e.getCause());
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                }
        );
    }
}