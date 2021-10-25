package com.example.phuc.iotdashboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import java.util.StringJoiner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemp;
    TextView txtHumi;
    TextView txtWind;
    TextView txtMois;
    ToggleButton btnLED;
    ToggleButton lightSwitch;
    Button windBut;
    Button tempButt;
    Button humiButt;
    Button moisButt;
    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        txtWind = findViewById(R.id.txtWindSpeed);
        txtMois = findViewById(R.id.txtMoisture);
        lightSwitch = findViewById(R.id.lightImg);
        windBut = findViewById(R.id.windButton);
        tempButt = findViewById(R.id.tempButton);
        humiButt = findViewById(R.id.humiButton);
        moisButt = findViewById(R.id.moisButton);

        windBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WindSpeed.class);
                startActivity(intent);
            }
        });
        tempButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Temperature.class);
                startActivity(intent);
            }
        });
        humiButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Humidity.class);
                startActivity(intent);
            }
        });
        moisButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Moisture.class);
                startActivity(intent);
            }
        });


        btnLED = findViewById(R.id.btnLED);
        btnLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Log.d("mqtt", "Button is ON");
                    sendDataToMQTT("PhucBKU/feeds/bbc-led", "1");
                    lightSwitch.setChecked(true);
                } else {
                    Log.d("mqtt", "Button if OFF");
                    sendDataToMQTT("PhucBKU/feeds/bbc-led", "0");
                    lightSwitch.setChecked(false);
                }
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String urlTemp = "https://io.adafruit.com/api/v2/PhucBKU/feeds/bbc-temp/data?limit=1";
        String urlHumi = "https://io.adafruit.com/api/v2/PhucBKU/feeds/bbc-humidity/data?limit=1";
        String urlMois = "https://io.adafruit.com/api/v2/PhucBKU/feeds/bbc-mois/data?limit=1";
        String urlWind = "https://io.adafruit.com/api/v2/PhucBKU/feeds/bbc-wind/data?limit=1";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlTemp, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            String value = object.getString("value");
                            txtTemp.setText(value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);

        JsonArrayRequest jsonArrayRequestHumi = new JsonArrayRequest(Request.Method.GET, urlHumi, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            String value = object.getString("value");
                            txtHumi.setText(value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequestHumi);

        JsonArrayRequest jsonArrayRequestMois = new JsonArrayRequest(Request.Method.GET, urlMois, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            String value = object.getString("value");
                            txtMois.setText(value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequestMois);

        JsonArrayRequest jsonArrayRequestWind = new JsonArrayRequest(Request.Method.GET, urlWind, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            String value = object.getString("value");
                            txtWind.setText(value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequestWind);

        startMQTT();
    }
    private void sendDataToMQTT(String topic, String mess){

        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = mess.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (Exception e){}

    }
    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(), "456789");
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
                if(topic.contains("bbc-temp")){
                    txtTemp.setText(message.toString());
                }
                if(topic.contains("bbc-humidity")){
                    txtHumi.setText(message.toString());
                }
                if(topic.contains("bbc-wind")){
                    txtWind.setText(message.toString());
                }
                if(topic.contains("bbc-mois")){
                    txtMois.setText(message.toString());
                }
//                if(topic.contains("bbc-led")){
//                    if(message.toString()=="1"){
//                        btnLED.setChecked(true);
//                    }else{
//                        btnLED.setChecked(false);
//                    }
//                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
