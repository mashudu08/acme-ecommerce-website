package com.example.beaconfinalapp.Network_Helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//    AsyncHttpClient asyncHttp = new AsyncHttpClient();
//            asyncHttp.get(url, new AsyncHttpResponseHandler() {
//@Override
//public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//        String str = new String(responseBody);
//        setResponse(str);
//        Log.d(TAG, str);
//        }
//
//@Override
//public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//        }
//        });

//Network util class used to make various API requests
public class NetworkUtil {

    //Network util will need a url String that will be used to make the request
    //The network util will return a String response
    //The network util uses Google's Volley library to make the request
    //The network util will run on a separate thread to the main
    //The Volley library needs an application context

    //Network util TAG
    private static final String TAG = "NetworkUtil";

    //#region Network util data members
    private final String url;
    private String response;
    private Context context;

    private OkHttpClient client = new OkHttpClient();


    //When creating the Network util helper pass in the url so that the request is made immediately
    public NetworkUtil(String url) {
        this.url = url;
        this.makeRequest(this.url);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //#endregion


    //#region helper methods

    //This method will make the API request to the Google API
    private String makeRequest(String urlPassed){

        //Build request url
        okhttp3.Request[] requests = {new Request.Builder().url(urlPassed).build()};
        String[] myResponse = new String[1];
        client.newCall(requests[0]).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                setResponse(response.body().string());
            }
        });

        this.response = myResponse[0];
        return myResponse[0];

        //Wait before setting the response
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    //This method will set this network util's string response
    private void setResponse(String response){

        this.response = response;
    }

    //This method will be used to get the string response to where this object was created
    public String getResponse(){
        return this.response;
    }

    //#endregion

}
