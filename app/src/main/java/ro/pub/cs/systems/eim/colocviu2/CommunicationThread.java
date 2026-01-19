package ro.pub.cs.systems.eim.colocviu2;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/*
 * Thread-ul care tratează o cerere de la UN client
 * Aici se rezolvă 3.b, 3.c și 3.d
 */
public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    /*
     * Metodă auxiliară pentru HTTP GET
     * Folosită la 3.b – acces Internet
     */
    private String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        conn.disconnect();
        return sb.toString();
    }

    /*
     * 3.c – prelucrarea cererii primite de la client
     * 3.d – transmiterea răspunsului către client
     */
    @Override
    public void run() {
        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out =
                    new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);

            // cererea clientului: oraș + tip informație
            String city = in.readLine();
            String type = in.readLine();

            if (city == null || type == null) {
                out.println("ERROR");
                socket.close();
                return;
            }

            city = city.trim();
            type = type.trim();

            /*
             * 3.a – verificare cache (obiect local)
             */
            WeatherForecastInformation info =
                    serverThread.getFromCache(city);

            /*
             * 3.b – acces Internet DOAR dacă nu există date local
             */
            if (info == null) {
                info = fetchWeather(city);
                if (info != null) {
                    serverThread.putInCache(city, info); // salvare în cache
                }
            }

            /*
             * 3.d – transmiterea răspunsului către client
             */
            if (info == null) {
                out.println("ERROR");
            } else {
                out.println(formatResponse(city, type, info));
            }

            socket.close();

        } catch (Exception e) {
            Log.e(Constants.TAG, "[COMM] " + e.getMessage());
        }
    }

    /*
     * 3.b – preluarea informațiilor meteorologice prin accesarea serviciului Internet
     */
    private WeatherForecastInformation fetchWeather(String city) {
        try {
            String encCity = URLEncoder.encode(city, "UTF-8");
            String url = String.format(Constants.WEATHER_URL, encCity);

            String json = httpGet(url);

            JSONObject root = new JSONObject(json);
            JSONObject cc =
                    root.getJSONArray("current_condition").getJSONObject(0);

            WeatherForecastInformation info =
                    new WeatherForecastInformation();

            info.setTemperature(cc.getString("temp_C"));
            info.setWindSpeed(cc.getString("windspeedKmph"));
            info.setPressure(cc.getString("pressure"));
            info.setHumidity(cc.getString("humidity"));
            info.setCondition(
                    cc.getJSONArray("weatherDesc")
                            .getJSONObject(0)
                            .getString("value")
            );

            return info;

        } catch (Exception e) {
            Log.e(Constants.TAG, "[HTTP] " + e.getMessage());
            return null;
        }
    }

    /*
     * 3.c – formatarea răspunsului în funcție de cererea clientului
     */
    private String formatResponse(String city, String type,
                                  WeatherForecastInformation info) {

        switch (type) {
            case Constants.TEMP:
                return city + " temperature: " + info.getTemperature() + " C";
            case Constants.WIND:
                return city + " wind: " + info.getWindSpeed() + " km/h";
            case Constants.COND:
                return city + " condition: " + info.getCondition();
            case Constants.PRESS:
                return city + " pressure: " + info.getPressure() + " hPa";
            case Constants.HUM:
                return city + " humidity: " + info.getHumidity() + " %";
            case Constants.ALL:
            default:
                return city + " | temp=" + info.getTemperature() +
                        "C, wind=" + info.getWindSpeed() +
                        "km/h, cond=" + info.getCondition() +
                        ", press=" + info.getPressure() +
                        "hPa, hum=" + info.getHumidity() + "%";
        }
    }
}
