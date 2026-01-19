package ro.pub.cs.systems.eim.colocviu2;

import android.util.Log;
import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String city;
    private final String type;
    private final ResultCallback callback;

    public interface ResultCallback {
        void onResult(String result);
    }

    public ClientThread(String address, int port, String city, String type, ResultCallback callback) {
        this.address = address;
        this.port = port;
        this.city = city;
        this.type = type;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(city);
            out.println(type);

            String response = in.readLine();
            socket.close();

            if (callback != null) callback.onResult(response);

        } catch (Exception e) {
            Log.e(Constants.TAG, "[CLIENT] " + e.getMessage());
            if (callback != null) callback.onResult("ERROR: " + e.getMessage());
        }
    }
}