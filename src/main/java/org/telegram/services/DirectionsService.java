package org.telegram.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.telegram.BuildVars;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Weather service
 * @date 20 of June of 2015
 */
public class DirectionsService {
    private static final String BASEURL = "https://maps.googleapis.com/maps/api/directions/json"; ///< Base url for REST
    private static final String APIIDEND = "&key=" + BuildVars.DirectionsApiKey;
    private static final String PARAMS = "&language=en&units=metric";
    private static final DateTimeFormatter dateFormaterFromDate = DateTimeFormatter.ofPattern("dd/MM/yyyy"); ///< Date to text formater
    private static volatile DirectionsService instance; ///< Instance of this class

    /**
     * Constructor (private due to singleton pattern)
     */
    private DirectionsService() {
    }

    /**
     * Singleton
     *
     * @return Return the instance of this class
     */
    public static DirectionsService getInstance() {
        DirectionsService currentInstance;
        if (instance == null) {
            synchronized (DirectionsService.class) {
                if (instance == null) {
                    instance = new DirectionsService();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }

    /**
     * Fetch the directions
     *
     * @param origin Origin address
     * @param destination Destination address
     * @return Destinations
     */
    public String getDirections(String origin, String destination) {
        String responseToUser;
        try {
            String completURL = BASEURL + "?origin=" + getQuery(origin) + "&destination=" +  getQuery(destination) + PARAMS + APIIDEND;
            HttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(completURL);
            HttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseContent = EntityUtils.toString(buf, "UTF-8");

            JSONObject jsonObject = new JSONObject(responseContent);
            if (jsonObject.getString("status").equals("OK")) {
                JSONObject route = jsonObject.getJSONArray("routes").getJSONObject(0);
                responseToUser = route.getJSONArray("legs").getJSONObject(0).getString("start_address");
                responseToUser += " is ";
                responseToUser += route.getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                responseToUser += " away from ";
                responseToUser += route.getJSONArray("legs").getJSONObject(0).getString("end_address"); // TODO Destination
                responseToUser += " and it takes ";
                responseToUser += route.getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
                responseToUser += " to arrive there following these directions:\n\n";
                responseToUser += getDirectionsSteps(route.getJSONArray("legs").getJSONObject(0).getJSONArray("steps"));
            } else {
                responseToUser = "Directions not found between " + origin + " and " + destination;
            }
        } catch (IOException e) {
            responseToUser = "Error fetching weather info";
        }
        return responseToUser;
    }

    private String getQuery(String address) throws UnsupportedEncodingException {
        return URLEncoder.encode(address, "UTF-8");
    }

    public String getDirectionsSteps(JSONArray steps) {
        String stepsStringify = "";
        for (int i = 0; i < steps.length(); i++) {
            stepsStringify += i + ".\t" + getDirectionForStep(steps.getJSONObject(i)) + "\n\n";
        }
        return stepsStringify;
    }

    private String getDirectionForStep(JSONObject jsonObject) {
        String direction = "";
        String htmlIntructions = Jsoup.parse(jsonObject.getString("html_instructions")).text();
        String duration = jsonObject.getJSONObject("duration").getString("text");
        String distance = jsonObject.getJSONObject("distance").getString("text");

        direction += htmlIntructions + " ";
        direction += " during ";
        direction += duration;
        direction += " (" + distance + ")";

        return direction;
    }
}
