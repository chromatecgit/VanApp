package br.art.chromatec.android.vanapp.service;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VehicleLocationService
        extends AsyncTask<String, Void, JSONObject> {
    private static final String ADDRESS_VEHICLE_LOCATION_SERVICE = "http://192.168.15.16:8080/vanapp-web/vehicles_location";
    private static final String TAG_RESPONSE_MESSAGE = "RESPONSE_MESSAGE";
    private static final String TAG_REST_SERVICE_URL = "REST_SERVICE_URL";
    private static final String TAG_JSON_OK = "JSON_OK_MESSAGE";
    private static final String TAG_JSON_ERROR = "JSON_ERROR_MESSAGE";

    public VehicleLocationService() {
    }

    protected JSONObject doInBackground(String... paramVarArgs) {
        HttpURLConnection connection = null;
        JSONObject jsonObject = null;
        try {
            URL url = new URL(ADDRESS_VEHICLE_LOCATION_SERVICE);
            Log.i(TAG_REST_SERVICE_URL, url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader response;
            connection.connect();

            StringBuilder sb = new StringBuilder();

            if (connection != null && connection.getResponseCode() == 200) {
                Log.i(TAG_RESPONSE_MESSAGE, connection.getResponseMessage());
                response = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while((line = response.readLine()) != null) {
                    sb.append(line);
                }
                jsonObject = new JSONObject(sb.toString());
                Log.i(TAG_JSON_OK, jsonObject.toString());
                response.close();

            } else if (connection != null && connection.getResponseCode() != 200) {
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
        return jsonObject;
    }

    protected void onPostExecute(JSONObject paramJSONObject) {

    }
}
