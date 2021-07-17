package com.example.testtask;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String URL_LINK = "http://demo3005513.mockable.io/api/v1/entities/getAllIds";
    private  TextView tVText;
    private ImageView imageView;
    private WebView webView;
    private int viewId = 0;
    private Button bttnNext ;

    private List<String> listB = null;
    private JSONObject jObj = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**initialisation  arrayList for id*/
        listB = new ArrayList<>();
        /**initialisation Button(NEXT)*/
        bttnNext = findViewById(R.id.bttnNext);
        bttnNext.setOnClickListener(this);
        /**initialisation TextView*/
        tVText = findViewById(R.id.tVText);
        /**initialisation ImageView*/
        imageView = findViewById(R.id.imageView);
        /**initialisation webView*/
        webView = findViewById(R.id.webView);
        /**Start parsing JSON A*/
        ParsingA parsingA = new ParsingA();
        parsingA.execute();
        /**Start parsing JSON B*/
        ParsingB parsingB = new ParsingB();
        parsingB.execute();

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.bttnNext:
                /**ID chosen */
                if (viewId<listB.size()-2)
                    viewId++;
                else
                    viewId=0;
                /**Start parsing JSON B*/
                ParsingB parsingB = new ParsingB();
                parsingB.execute();
                break;
        }

    }

    class ParsingB extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        private JSONArray names;
        private JSONArray values;

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            java.io.BufferedReader in = null;

            try {
                /**Load JSON B*/
                url = new URL("https://demo3005513.mockable.io/api/v1/object/" + listB.get(viewId));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                in = new java.io.BufferedReader(new java.io.InputStreamReader(urlConnection.getInputStream()));

                String line;

                for (; (line = in.readLine()) != null; ) {
                    jObj = new JSONObject(line);
                }

                names = jObj.names();
                values = jObj.toJSONArray(names);
                in.close();


            } catch (IOException | JSONException e) {
                e.printStackTrace();

            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /**Change GUI*/
            for (int i = 0; i < values.length() - 1; i++) {
                try {

                    if (names.getString(i).equals("type"))
                        if (values.getString(i).equals("text")) {
                            tVText.setText(values.getString(i+1));
                            /**Visibility*/
                            tVText.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                        } else if (values.getString(i).equals("webview")) {
                            /**Visibility*/
                            webView.loadUrl(
                                    values.getString(i+1)
                            );
                            tVText.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                        } else if (values.getString(i).equals("image")) {
                            Picasso.get().load(values.getString(i+1)).into(imageView);
                            /**Visibility*/
                            tVText.setVisibility(View.GONE);
                            webView.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("MainActivity", "CRASH " );
                }
            }

        }
    }


    class ParsingA extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            java.io.BufferedReader in = null;

            try {
                url = new URL(URL_LINK);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                in = new java.io.BufferedReader(new java.io.InputStreamReader(urlConnection.getInputStream()));
                String line;
                for (; (line = in.readLine()) != null; ) {
                    jObj = new JSONObject(line);
                }

                JSONArray names = jObj.names();
                JSONArray values = jObj.toJSONArray(names);

                for(int i=0; i<values.length(); i++) {
                    if (names.getString(i).equals("data")) {
                        for(int j=0;j<values.getJSONArray(i).length();j++)
                            listB.add(values.getJSONArray(i).getJSONObject(j).getString("id"));//.getString(j));
                    }
                }
                in.close();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }








}