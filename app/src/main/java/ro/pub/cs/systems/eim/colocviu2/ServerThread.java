package ro.pub.cs.systems.eim.colocviu2;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private final int port;

    // OBIECTUL LOCAL cerut la 3.a
    private final HashMap<String, WeatherForecastInformation> data = new HashMap<>();

    public ServerThread(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(Constants.TAG, "ServerSocket error: " + e.getMessage());
            serverSocket = null;
        }
    }

    public ServerSocket getServerSocket() { return serverSocket; }

    // citire cache 3.a
    public WeatherForecastInformation getFromCache(String city) {
        synchronized (data) {
            return data.get(city);
        }
    }


    // scriere cache 3.a
    public void putInCache(String city, WeatherForecastInformation info) {
        synchronized (data) {
            data.put(city, info);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER] waiting...");


                Socket socket = serverSocket.accept(); // strict pentru 3a


                new CommunicationThread(this, socket).start();
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "[SERVER] " + e.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try { serverSocket.close(); } catch (IOException ignored) {}
        }
    }
}
