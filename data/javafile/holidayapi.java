package routesearch.data.javafile;

// HolidayAPIFetcher.java
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class holidayapi {
    private static String holidayData;

    public static void HolidayData() throws IOException {
        URL url = new URL("https://holidays-jp.github.io/api/v1/date.json");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream();
                 Scanner scanner = new Scanner(inputStream)) {

                StringBuilder response = new StringBuilder();

                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                // 取得したデータを変数に格納
                holidayData = response.toString();
            }
        } else {
            throw new IOException("Failed to retrieve holiday data. HTTP error code: " + responseCode);
        }
    }

    public static String getHolidayData() {
        return holidayData;
    }
}

