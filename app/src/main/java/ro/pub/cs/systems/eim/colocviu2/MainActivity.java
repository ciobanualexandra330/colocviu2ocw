package ro.pub.cs.systems.eim.colocviu2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button connectButton;

    private EditText addressEditText;
    private EditText clientPortEditText;
    private EditText cityEditText;

    private Spinner infoTypeSpinner;
    private Button getWeatherButton;

    private TextView resultTextView;

    private ServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // IDs din XML-ul tău
        serverPortEditText = findViewById(R.id.editTextText);
        connectButton = findViewById(R.id.button);

        addressEditText = findViewById(R.id.editText1);
        clientPortEditText = findViewById(R.id.editText2);
        cityEditText = findViewById(R.id.editText3);

        infoTypeSpinner = findViewById(R.id.information_type_spinner);
        getWeatherButton = findViewById(R.id.button1);

        resultTextView = findViewById(R.id.result_text_view);

        connectButton.setOnClickListener(v -> {

            if (serverThread != null) {
                Toast.makeText(getApplicationContext(), "Server already started!", Toast.LENGTH_SHORT).show();
                return;
            }

            String serverPortStr = serverPortEditText.getText().toString().trim();
            if (serverPortStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            int serverPort = Integer.parseInt(serverPortStr);

            serverThread = new ServerThread(serverPort);
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "Could not create server thread!");
                Toast.makeText(getApplicationContext(), "Could not start server!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread.start();
            Toast.makeText(getApplicationContext(), "Server started on port " + serverPort, Toast.LENGTH_SHORT).show();
        });

        getWeatherButton.setOnClickListener(v -> {
            // incepe 2
            String address = addressEditText.getText().toString().trim();
            String clientPortStr = clientPortEditText.getText().toString().trim();
            String city = cityEditText.getText().toString().trim();
            String type = infoTypeSpinner.getSelectedItem().toString();

            if (address.isEmpty() || clientPortStr.isEmpty() || city.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Address/Port/City must be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            int clientPort = Integer.parseInt(clientPortStr);

            resultTextView.setText("Loading...");
            // pana aici e 2

            // incepe 4 cu client
            ClientThread clientThread = new ClientThread(address, clientPort, city, type, result -> {
                runOnUiThread(() -> resultTextView.setText(result));
            });
            clientThread.start();

        });
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "onDestroy()");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}