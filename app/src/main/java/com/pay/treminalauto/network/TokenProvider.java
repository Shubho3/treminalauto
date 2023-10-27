package com.pay.treminalauto.network;

import androidx.annotation.NonNull;

import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback;
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider;
import com.stripe.stripeterminal.external.models.ConnectionTokenException;

/**
 * A simple implementation of the [ConnectionTokenProvider] interface. We just request a
 * new token from our backend simulator and forward any exceptions along to the SDK.
 */
public class TokenProvider implements ConnectionTokenProvider {
    @Override
    public void fetchConnectionToken(@NonNull ConnectionTokenCallback callback) {
        try {
            final String token = ApiClient.createConnectionToken();
            callback.onSuccess(token);
        } catch (Exception e) {
            callback.onFailure((ConnectionTokenException) e);
        }
    }
}
