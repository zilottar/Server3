package com.example.server;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://androiddocs.ru/api/friends.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;
            String secondName = "";

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray friends = dataJsonObj.getJSONArray("friends");

                // 1. достаем инфо о втором друге - индекс 1
                JSONObject secondFriend = friends.getJSONObject(1);
                secondName = secondFriend.getString("name");
                Log.d(LOG_TAG, "Второе имя: " + secondName);

                // 2. перебираем и выводим контакты каждого друга
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject friend = friends.getJSONObject(i);

                    JSONObject contacts = friend.getJSONObject("contacts");

                    String phone = contacts.getString("mobile");
                    String email = contacts.getString("email");
                    String skype = contacts.getString("skype");

                    Log.d(LOG_TAG, "phone: " + phone);
                    Log.d(LOG_TAG, "email: " + email);
                    Log.d(LOG_TAG, "skype: " + skype);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}