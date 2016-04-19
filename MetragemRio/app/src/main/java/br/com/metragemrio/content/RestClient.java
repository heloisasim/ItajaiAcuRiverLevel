package br.com.metragemrio.content;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RestClient {

    public static Request create() {

        int timeout = 30;
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(timeout, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(timeout, TimeUnit.SECONDS);
        System.setProperty("http.keepAlive", "false");
        Executor executor = Executors.newCachedThreadPool();

        try {
            RestAdapter restAdapter = new RestAdapter.Builder()//
                    .setClient(new OkClient(okHttpClient))//
                    .setEndpoint("http://54.232.230.246:5001")//
//                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setExecutors(executor, executor).build();
            return restAdapter.create(Request.class);

        } catch (Exception e) {
			e.printStackTrace();
            return null;
        }
    }

}
