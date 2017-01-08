package br.art.chromatec.android.vanapp.service;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by Chromatec on 15/12/2016.
 */
public class VehicleServices extends AsyncTask<String, Void, JSONArray> {
    private static final String ADDRESS_VEHICLE_DATA_SERVICES = "http://192.168.15.16:8080/vanapp-web/vehicles";
    private static final String TAG_CONNECTION_CODE = "CONNECTION_CODE";
    private static final String TAG_RESPONSE_MESSAGE = "RESPONSE_MESSAGE";
    private static final String TAG_SERVICE_FINISHED = "SERVICE EXEC FINISHED";
    private static final String TAG_REST_SERVICE_URL = "REST_SERVICE_URL";
    private static final String TAG_JSON_OK = "JSON_OK_MESSAGE";
    private static final String TAG_JSON_ERROR = "JSON_ERROR_MESSAGE";

    public VehicleServices() {
    }

    @Override
    public JSONArray doInBackground(String... paramVarArgs) {
        HttpURLConnection connection = null;
        JSONArray jsonArray = null;
        try {
            URL url = new URL(ADDRESS_VEHICLE_DATA_SERVICES);
            Log.i(TAG_REST_SERVICE_URL, url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader response;

            StringBuilder sb = new StringBuilder();
            Log.i(TAG_CONNECTION_CODE, String.valueOf(connection.getResponseCode()));
            if (connection.getResponseCode() == 200) {
                Log.i(TAG_RESPONSE_MESSAGE, connection.getResponseMessage());
                response = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while((line = response.readLine()) != null) {
                    sb.append(line);
                }
                jsonArray = new JSONArray(sb.toString());
                Log.i(TAG_JSON_OK, jsonArray.toString());
                response.close();

            } else {
                Log.i(TAG_RESPONSE_MESSAGE, connection.getResponseMessage());
                response = new BufferedReader(new InputStreamReader(
                        connection.getErrorStream()));
                String line;
                while((line = response.readLine()) != null) {
                    sb.append(line);
                }
                response.close();
                Log.i(TAG_JSON_ERROR, sb.toString());

            }

        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jsonArray;
    }

    public void onPostExecute(JSONArray jsonArray) {
        //Log.i(TAG_SERVICE_FINISHED, jsonObject.toString());
    }
}
