package com.pay.treminalauto;

import android.util.Log;

import com.stripe.stripeterminal.external.callable.TerminalListener;
import com.stripe.stripeterminal.external.models.ConnectionStatus;
import com.stripe.stripeterminal.external.models.PaymentStatus;
import com.stripe.stripeterminal.external.models.Reader;

import org.jetbrains.annotations.NotNull;

public class TerminalEventListener implements TerminalListener {
    private static final String TAG = TerminalEventListener.class.toString();

    @Override
    public void onUnexpectedReaderDisconnect(@NotNull Reader reader) {
        Log.i("UnexpectedDisconnect", reader.getSerialNumber() != null ?
                reader.getSerialNumber() : "reader's serialNumber is null!");
    }

    @Override
    public void onConnectionStatusChange(@NotNull ConnectionStatus status) {
        Log.i("ConnectionStatusChange", status.toString());
    }

    @Override
    public void onPaymentStatusChange(@NotNull PaymentStatus status) {
        Log.i("PaymentStatusChange", status.toString());
    }
}
