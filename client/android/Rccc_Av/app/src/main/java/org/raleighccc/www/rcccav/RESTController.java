package org.raleighccc.www.rcccav;

import android.os.AsyncTask;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

/**
 * This could be the RESTful controller
 * Created by pinwu on 10/11/2015.
 */
public class RESTController  {

    // first batch of commands (URIs)
    public static final String AVSTATUS = "/rcccav/status";
    public static final String AVSYSON = "/rcccav/system/on";
    public static final String AVSYSOFF = "/rcccav/system/off";
    public static final String AVSYSREBOOT = "/rcccav/system/reboot";
    public static final String AVSYSFORCEOFF = "/rcccav/system/force_off";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private String hostIP;
    //private boolean controllerUp;

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public String getHostIP() {
        return hostIP;
    }

    /*
     * this method should be checking if the controller box is up
     */
    public boolean isControllerBoxUp() {
        return false;
    }

    /*
     * this method should check the controller Restful service is up
     */
    public boolean isControllerReachable() {
        String restURL = this.hostIP + AVSTATUS ;
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            // HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(restURL)
                    .openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /*
     * now the system up command
     */
    public void avsystemUp() {
        String systemUpURL = "http://" + this.hostIP + AVSYSON;
        client.get(systemUpURL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("statusCode : " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("statusCode : " + statusCode);
            }
        });

    }

    public void avsystemOff() {
        String systemOffURL = "http://" + this.hostIP + AVSYSOFF;

        //AsyncHttpClient client = new AsyncHttpClient();
        client.get(systemOffURL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // this will get executed when we are good
                System.out.println("statusCode : " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("statusCode : " + statusCode);
                // may need to throw an error here...
            }
        });

    }

    /*
    @Override
    protected String doInBackground(String... urls) {
        return "pass";
    }
    */
}
