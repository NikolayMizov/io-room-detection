package nl.fontys.s3;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpDataSender {
    public static void sendData(String serverUrl, String macAddress, int peopleCount) {
        try {
            String jsonPayload = "{ \"macAddress\": \"" + macAddress + "\", \"nrOfOccupants\": " + peopleCount + " }";
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write json payload to request
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
