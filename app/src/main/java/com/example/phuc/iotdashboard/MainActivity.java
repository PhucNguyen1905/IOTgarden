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
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

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
