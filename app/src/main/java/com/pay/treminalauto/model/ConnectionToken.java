package com.pay.treminalauto.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * A one-field data class used to handle the connection token response from our backend
 */
public class ConnectionToken implements Serializable {
    @SerializedName("secret")
    @NotNull private final String secret;

    public ConnectionToken(@NotNull String secret) {
        this.secret = secret;
    }

    @NotNull
    public String getSecret() {
        return secret;
    }
}
