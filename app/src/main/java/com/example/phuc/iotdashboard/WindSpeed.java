package com.example.phuc.iotdashboard;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.lang.Float.parseFloat;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WindSpeed extends AppCompatActivity {

    SpeedView speedwind;
    MQTTHelper mqttHelper;
    LineChart mchart ;
    int count=0;
    int flag = 0;
    float speed2;
    String str = "";
    ArrayList<Entry> yValues = new ArrayList<>();
    LineDataSet set1 = new LineDataSet(yValues,"Speed wind");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windspeed);
        speedwind = (SpeedView) findViewById(R.id.speedView);
        mchart = (LineChart) findViewById(R.id.linechart);
        mchart.setDragEnabled(true);
        mchart.setScaleEnabled(true);

        set1.setFillAlpha(200);
        set1.setColor(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(20f);
        set1.setValueTextColor(Color.YELLOW);


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://io.adafruit.com/api/v2/PhucBKU/feeds/bbc-wind/data?limit=10";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=response.length()-1 ; i >=0 ;i--)
                        {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String value = object.getString("value");
                                drwawchart(Integer.parseInt(value));
                                speedwind.speedTo(Integer.parseInt(value),1000);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WindSpeed.this,"Error!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);


        startMQTT();

    }
    public void drwawchart(float speed2)
    {
        Entry tmp = new Entry(count,speed2);
        set1.addEntry(tmp);
        LineData data= new LineData(set1);
        count = count+1;
        mchart.setData(data);
    }
    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(), "1236");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("Mqtt", "Kết nối thành công");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Mqtt", "Received: " + message.toString());
                if(topic.contains("bbc-wind")){
                    int speed1 = Integer.parseInt(message.toString());
                    speedwind.speedTo(speed1,1000);
                    Log.d("count","!");
                    speed2 = Float.parseFloat(message.toString());
                    drwawchart(speed2);

                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

        });
    }

};