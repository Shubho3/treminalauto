package com.pay.treminalauto.network;

import android.util.Log;


import com.pay.treminalauto.model.ConnectionToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    HttpLoggingInterceptor interceptor = new
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS) // connect timeout
            .writeTimeout(25, TimeUnit.SECONDS) // write timeout
            .readTimeout(25, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            ; // read timeout
    OkHttpClient client = builder.build();


    private static final Retrofit mRetrofit = new Retrofit.Builder()
            //  .baseUrl("https://stripe-terminal-backend-code.onrender.com")
             .baseUrl("https://wkreative.com/admin-orkiosk-com/webservice/")
         //.baseUrl("https://example-terminal-backend-13wi.onrender.com")
            .client(new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final BackendService mService = mRetrofit.create(BackendService.class);

    @NotNull
    public static String createConnectionToken() {
        try {
            final Response<ConnectionToken> result = mService.getConnectionToken().execute();
            if (result.isSuccessful() && result.body() != null) {
                Log.e("TAG", "createConnectionToken: --------------------" + result.body().getSecret());
                return result.body().getSecret();
            } else {
                Log.e("TAG", "createConnectionToken: --------------------" + result.body());

                return "Creating connection token failed";

                //throw new ConnectionTokenException("Creating connection token failed");
            }
        } catch (Exception e) {
            Log.e("TAG", "createConnectionToken: --------------------" + e.getMessage());
            Log.e("TAG", "createConnectionToken: --------------------" + e.getLocalizedMessage());
            Log.e("TAG", "createConnectionToken: --------------------" + e.getCause());

            return "Creating connection token failed";

            // throw new ConnectionTokenException("Creating connection token failed", e);
        }
    }

    public static @NotNull String createLocation(
            String displayName,
            String city,
            String country,
            String line1,
            String line2,
            String postalCode,
            String state
    ) {
        // try {
           /* final Response<ConnectionToken> result = mService.create_location(displayName).execute();
            if (result.isSuccessful() && result.body() != null) {
                return result.body().getSecret();
            } else {
                throw new ConnectionTokenException("Creating connection token failed");
            }
        } catch (IOException e) {
            throw new ConnectionTokenException("Creating connection token failed", e);
        } catch (ConnectionTokenException e) {
            thro
           w new RuntimeException(e);
        }*/
        return "data";
    }

    public static ResponseBody capturePaymentIntent(@NotNull String id) throws IOException {
      return   mService.capturePaymentIntent(id).execute().body();
    }

    public static void cancelPaymentIntent(
            String id,
            Callback<Void> callback
    ) {
        mService.cancelPaymentIntent(id).enqueue(callback);
    }

}
