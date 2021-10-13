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
import android.widget.ToggleButton;

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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Temperature extends AppCompatActivity{

    private LineChart mChart;
    private int mFillColor = Color.argb(150, 51, 181, 229);
    MQTTHelper mqttHelper;
    TextView txtTemp;
    ArrayList<Entry> yVals = new ArrayList<>();
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        txtTemp = findViewById(R.id.Temperature);
        txtTemp.setText("30" + "°C");

        /*for (int i = 0; i < 24; i++){
            int value = i%2==0?10:5;
            yVals.add(new Entry(i, value));
        }
        drawChart(yVals);*/
        yVals.add(new Entry(0, 0));
        drawChart(yVals);
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
        leftAxis.setAxisMaximum(50);
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

                if(topic.contains("bbc-temp")){
                    txtTemp.setText(message.toString() + "°C");
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