package com.example.phuc.iotdashboard;

/**
 * Created by Phuc on 10/11/2021.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Float.parseFloat;


public class Humidity extends AppCompatActivity{

    private LineChart mChart;
    private int mFillColor = Color.argb(150, 51, 181, 229);
    MQTTHelper mqttHelper;
    TextView txtTemp;
    ArrayList<Entry> yVals = new ArrayList<>();
    int count = 1;
    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        txtTemp = findViewById(R.id.Temperature);


        /*for (int i = 0; i < 24; i++){
            int value = i%2==0?10:5;
            yVals.add(new Entry(i, value));
        }
        drawChart(yVals);*/
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://io.adafruit.com/api/v2/PhucBKU/feeds/bbc-humidity/data?limit=10";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=response.length()-1 ; i >=0 ;i--)
                        {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String value = object.getString("value");
                                Entry tmp = new Entry(count,parseFloat(value));
                                yVals.add(tmp);
                                count = count+1;
                                txtTemp.setText(value + "%");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        drawChart(yVals);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Humidity.this,"Error!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);

        startMQTT();
    }

    private void drawChart(ArrayList<Entry> yVals){
        mChart = (LineChart) findViewById(R.id.LineChart);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setGridBackgroundColor(Color.CYAN);
        mChart.setDrawGridBackground(true);

        mChart.setDrawBorders(true);
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(false);

        Legend l = mChart.getLegend();
        l.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaximum(100);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawGridLines(false);

        LineDataSet dataSet;

        dataSet = new LineDataSet(yVals, "Data set1");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.GRAY);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(3f);
        dataSet.setFillAlpha(255);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.GRAY);
        //setData(24, 20);
        LineData data = new LineData(dataSet);
        data.setDrawValues(false);

        mChart.setData(data);
    }

    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(), "4321");

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqtt", "Kết nối thành công");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("mqtt", "Received " + message.toString());
                //if(topic.equals("abc" == true)

                if(topic.contains("bbc-humidity")){
                    txtTemp.setText(message.toString() + "%");
                    if(yVals.size() == 12){
                        yVals.remove(0);
                        yVals.add(new Entry(count, Float.parseFloat(message.toString())));
                    }
                    else {
                        yVals.add(new Entry(count, Float.parseFloat(message.toString())));
                    }

                    count += 1;
                }
                //System.out.println(yVals);
                drawChart(yVals);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

   /*private void setData(int count, float range){
        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i< count; i++){
            float val = (float) (Math.random()*range) + 10;
            yVals.add(new Entry(i, val));
        }
        ArrayList<Entry> yVals1 = new ArrayList<>();
        for (int i = 0; i< count; i++){
            float val = (float) (Math.random()*range) + 20;
            yVals1.add(new Entry(i, val));
        }
        LineDataSet set1, set2;
        set1 = new LineDataSet(yVals, "Data set1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.BLACK);
        set1.setDrawCircles(false);
        set1.setLineWidth(3f);
        set1.setFillAlpha(255);
        set1.setDrawFilled(true);
        set1.setFillColor(Color.CYAN);
        set2 = new LineDataSet(yVals1, "Data set2");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(Color.RED);
        set2.setDrawCircles(false);
        set2.setLineWidth(3f);
        set2.setFillAlpha(255);
        set2.setDrawFilled(true);
        set2.setFillColor(Color.WHITE);
        LineData data = new LineData(set1);
        data.setDrawValues(false);
        mChart.setData(data);
    }
    */
}