package com.example.mysensors;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.io.UnsupportedEncodingException;

public class ArduinoTestActivity extends AppCompatActivity implements SensorEventListener {

    private MqttAndroidClient client;
    final String MQTT_HOST = "tcp://broker.hivemq.com:8883";
    final String sub_topic = "arduino/in/mamn01/groupD/androidSensor";
    final String pub_topic = "arduino/out/mamn01/groupD/arduinoSensor";
    final String pub_message = "Hello World!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_test);

        connect();
    }

    public void subscribe(MqttAndroidClient client , String topic){
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void publish(MqttAndroidClient client, String topic, String payload){
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(MqttAndroidClient client, String topic, byte payload){
        byte[] encodedPayload = new byte[]{payload};
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(MqttAndroidClient client, String topic, int payload){
        byte[] encodedPayload = new byte[]{(byte)payload};
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        String clientId = MqttClient.generateClientId();
        Log.d("ClientId", clientId);
        client =  new MqttAndroidClient(this.getApplicationContext(), MQTT_HOST, clientId);
        Log.d("Client", client.getClientId());

        if (client == null) return;

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setCleanSession(false);
        try {
            IMqttToken token = client.connect(options);
            //IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("file", "onSuccess");
                    //publish(client,"payload");
                    subscribe(client, sub_topic);
                    subscribe(client,"bmp");
                    client.setCallback(new MqttCallback() {
                        TextView tt = (TextView) findViewById(R.id.textview_first);
                        @Override
                        public void connectionLost(Throwable cause) {

                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.d("file", message.toString());

                            if (topic.equals(sub_topic)){
                                tt.setText(message.toString());
                            }

                        }
                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("file", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String val = String.valueOf(event.values[0]);
        TextView th = (TextView) findViewById(R.id.textview_first2);
        th.setText(val);
        publish(client, pub_topic, String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}