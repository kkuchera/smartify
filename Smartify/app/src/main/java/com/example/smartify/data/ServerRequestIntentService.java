package com.example.smartify.data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class ServerRequestIntentService extends IntentService {

    public static final String STATUS = "com.example.smartify.status";
    public static final String VALUE = "com.example.smartify.value";

    private static final String TAG = ServerRequestIntentService.class.getSimpleName();

    /**
     * Integers to report if the service executed successfully or not.
     */
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;

    /**
     * String representation of the actions that can be requested in this IntentService.
     */
    private static final String ACTION_GET = "com.example.smartify.action.GET";
    private static final String ACTION_POST = "com.example.smartify.action.POST";

    /**
     * String representation of the extra data that can be given to this IntentService.
     */
    private static final String EXTRA_RECEIVER = "com.example.smartify.extra.RECEIVER";
    private static final String EXTRA_URL = "com.example.smartify.extra.URL";
    private static final String EXTRA_ACCESS_TOKEN = "com.example.smartify.extra.ACCESS_TOKEN";
    private static final String EXTRA_ARG = "com.example.smartify.extra.ARG";



    public ServerRequestIntentService() {
        super(TAG);
    }


    /**
     * Starts this service to perform action Get with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context The context of the application
     * @param resultReceiver Receiver to call with result
     * @param url The url on which to perform the request
     * @param accessToken The access token used to authenticate the request
     */
    public static void startActionGet(Context context, ResultReceiver resultReceiver, URL url,
                                      String accessToken) {
        Intent intent = new Intent(context, ServerRequestIntentService.class);
        intent.setAction(ACTION_GET);
        intent.putExtra(EXTRA_RECEIVER, resultReceiver);
        intent.putExtra(EXTRA_URL, url.toString() + "?access_token="+accessToken);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Post with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context The context of the application
     * @param resultReceiver Receiver to call with result
     * @param url The url on which to perform the request
     * @param accessToken The access token used to authenticate the request
     * @param arg The argument for the request
     */
    public static void startActionPost(Context context, ResultReceiver resultReceiver, URL url,
                                      String accessToken, String arg) {
        Intent intent = new Intent(context, ServerRequestIntentService.class);
        intent.setAction(ACTION_POST);
        intent.putExtra(EXTRA_RECEIVER, resultReceiver);
        intent.putExtra(EXTRA_URL, url.toString());
        intent.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
        intent.putExtra(EXTRA_ARG, arg);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
            final String action = intent.getAction();
            if (ACTION_GET.equals(action)) {
                try {
                    final URL url = new URL(intent.getStringExtra(EXTRA_URL));
                    handleActionGet(receiver, url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_POST.equals(action)) {
                try {
                    final URL url = new URL(intent.getStringExtra(EXTRA_URL));
                    final String accessToken = intent.getStringExtra(EXTRA_ACCESS_TOKEN);
                    final String arg = intent.getStringExtra(EXTRA_ARG);
                    handleActionPost(receiver, url, accessToken, arg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleActionGet(ResultReceiver resultReceiver, URL url) {
        HttpsURLConnection urlConnection = null;
        Bundle bundle = new Bundle();
        Log.i(TAG, url.toString());
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            int httpStatus = urlConnection.getResponseCode();
            bundle.putInt(STATUS, httpStatus);
            switch (httpStatus) {
                case HttpsURLConnection.HTTP_OK:
                    JSONObject jsonObject = readJson(urlConnection);
                    bundle.putInt(VALUE, jsonObject.getInt("result"));
                    resultReceiver.send(RESULT_OK, bundle);
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    resultReceiver.send(RESULT_OK, bundle);
                    break;
                default:
                    resultReceiver.send(RESULT_ERROR, bundle);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            resultReceiver.send(RESULT_ERROR, bundle);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void handleActionPost(ResultReceiver resultReceiver, URL url, String accessToken,
                                  String arg) {
        int httpStatus = postRequest(url, accessToken, arg);
        Bundle bundle = new Bundle();
        bundle.putInt(STATUS, httpStatus);
        Log.i(TAG, url.toString());
        switch (httpStatus) {
            case HttpsURLConnection.HTTP_OK:
                resultReceiver.send(RESULT_OK, bundle);
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                resultReceiver.send(RESULT_OK, bundle);
                break;
            default:
                resultReceiver.send(RESULT_ERROR, bundle);
        }
    }

    private static int postRequest(URL url, String accessToken, String arg) {
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream())); //Trick to be able to use readline, slow, converts bytes to chars
            out.write("access_token=" + accessToken + "&" + "arg=" + arg);
            out.close();
            return urlConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return -1;
    }

    /**
     * Reads JSON-encoded data from an HttpsUrlConnection.
     *
     * @param urlConnection The HttpURLConnection to read from.
     * @return JSON response from server.
     * @throws IOException If there is a problem with the url connection.
     * @throws JSONException If there is an error in the JSON formatting of the server's response.
     */
    private static JSONObject readJson(HttpURLConnection urlConnection) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); //Trick to be able to use readline, slow, converts bytes to chars
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        return new JSONObject(response.toString());
    }
}
