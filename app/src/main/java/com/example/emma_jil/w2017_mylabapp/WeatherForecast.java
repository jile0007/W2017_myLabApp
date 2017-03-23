package com.example.emma_jil.w2017_mylabapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "WeatherForecast";
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    // screen variables
    ImageView weatherPic;
    TextView currentTemperature;
    TextView minimumTemperature;
    TextView maximumTemperature;


    String urlString = "http://api.openweathermap.org/data/2.5/"
            +"weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d"
            +"97d0f6a&mode=xml&units=metric";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        Log.i(ACTIVITY_NAME, "in onCreate()");

        // initializing variables
        weatherPic = (ImageView)findViewById(R.id.weatherForecastImage);
        currentTemperature = (TextView)findViewById(R.id.currentTemp);
        minimumTemperature = (TextView)findViewById(R.id.minTemp);
        maximumTemperature = (TextView)findViewById(R.id.maxTemp);
        progressBar = (ProgressBar)findViewById(R.id.weatherProgressBar);

        // make progress bar visible
        progressBar.setVisibility(View.VISIBLE);

        // Start operation in background: android dev site
        new ForecastQuery().execute(urlString);
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String>{
        // sub-class variables
        private static final String ACTIVITY_NAME = "ForecastQuery";
        private String minT;
        private String maxT;
        private String currentT;
        private Bitmap weatherImage;
        private String iconName;

        // seemed to be needed from example
        private XmlPullParserFactory xmlFactoryObject;

        protected String doInBackground(String... urls){
            Log.i(ACTIVITY_NAME, "in doInBackground");
            // 7.1 Copy code from link #1 example
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                InputStream is = connection.getInputStream();
                Log.i(ACTIVITY_NAME, "in doInBackground: connected");
                // 7.2 getting the Parser to read the data
                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser parser = xmlFactoryObject.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);

                // 8. Iterate through XML tags to get temp info
                while(parser.next() != XmlPullParser.END_DOCUMENT){
                    if(parser.getEventType() != XmlPullParser.START_TAG){
                        continue;
                    }
                    String infoName = parser.getName();
                    Log.i(ACTIVITY_NAME, "in doInBackground: Tag: " + infoName);
                    if (infoName.equals("temperature")){
                        Log.i(ACTIVITY_NAME, "temperature info found");
                        // loop through attributes inside tag
                        int attrCount = parser.getAttributeCount();
                        Log.i(ACTIVITY_NAME, infoName+ " has " + attrCount + " values");
                        // finding attribute value
                        currentT = parser.getAttributeValue(null, "value");
                        publishProgress(25);
                        minT = parser.getAttributeValue(null, "min");
                        publishProgress(50);
                        maxT = parser.getAttributeValue(null, "max");
                        publishProgress(75);
                    } else if (infoName.equals("weather")){
                        // 9. Looking for icon for pic
                        iconName = parser.getAttributeValue(null, "icon");
                        try {
                            URL u = new URL("http://openweathermap.org/img/w/"
                                    + iconName + ".png");
                            // 11.Saving image to file or upload from file
                            String fileName = iconName  + ".png";
                            if(fileExistance(fileName) /* file exist */ ){
                                Log.i(ACTIVITY_NAME, infoName+" image taken from file.");
                                FileInputStream fileInputStream = null;
                                try{ fileInputStream = new FileInputStream(
                                        getFileStreamPath(fileName)
                                ); }
                                catch (FileNotFoundException e){
                                    Log.i(ACTIVITY_NAME, "FileNotFoundException");
                                }
                                weatherImage = BitmapFactory.decodeStream(fileInputStream);
                            } else /* it doesn't exist */ {
                                Log.i(ACTIVITY_NAME, infoName+" image downloaded from URL");
                                weatherImage = getImage(u);
                                FileOutputStream outputStream = openFileOutput(
                                        iconName+".png", Context.MODE_PRIVATE
                                );
                                weatherImage.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }
                            publishProgress(100);
                        } catch (MalformedURLException e){
                            Log.i(ACTIVITY_NAME, "in setWeatherPic: error");
                        }
                    }
                }
                // close input & disconnect
                is.close();
                connection.disconnect();
                return null;
            } catch (IOException e) {
                Log.i(ACTIVITY_NAME, "IOException");
                return null;
            } catch (XmlPullParserException e){
                Log.i(ACTIVITY_NAME, "XmlPullParserException");
                return null;
            } catch (Exception e){
                Log.i(ACTIVITY_NAME, "Some other Exception");
                return null;
            }
        }

        protected void onProgressUpdate(Integer...values){
            // 10. set progress bar visibility and progression
            Log.i(ACTIVITY_NAME, "in onProgressUpdate(" + values[0] + ")");
            progressBar.setProgress(values[0]);
        }

        protected void onPostExecute(String s){
            weatherPic.setImageBitmap(weatherImage);
            currentTemperature.setText("Current: " + currentT + "°C");
            minimumTemperature.setText("Min: " + minT + "°C");
            maximumTemperature.setText("Max: " + maxT + "°C");
            progressBar.setVisibility(View.INVISIBLE);
        }

        // 11. OpenWeatherMap to get weather pic
        private Bitmap getImage(URL url){
            Log.i(ACTIVITY_NAME, "in getImage()");
            HttpURLConnection connect = null;
            try {
                connect = (HttpURLConnection) url.openConnection();
                connect.connect();
                int responseCode = connect.getResponseCode();
                if (responseCode == 200){
                    return BitmapFactory.decodeStream(connect.getInputStream());
                } else {
                    return null;
                }
            } catch(Exception e){
                Log.e(ACTIVITY_NAME, "in getImage: " + e);
                return null;
            } finally {
                if (connect != null){
                    connect.disconnect();
                }
            }

        }

        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }
    }
}
