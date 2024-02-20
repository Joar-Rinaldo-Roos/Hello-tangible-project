package com.example.mysensors;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

public class MqttTestActivity extends AppCompatActivity {
    final String MQTT_HOST = "tcp://broker.hivemq.com:1883";
    final String sub_topic = "arduino/in/mamn01/groupD/androidSensor";
    final String pub_topic = "arduino/out/mamn01/groupD/arduinoSensor";
    final String pub_message = "Hello World!";

    MqttAndroidClient mqttAndroidClient;

    private IMqttToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_test);

        Log.d("Bout to connect", "connecting");
        connect();
    }

    public void subscribe(MqttAndroidClient client, String topic) {
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Subscribe", "Subscription successful!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Subscribe", "Subscription failed!");
                }
            });

            // Set up message listener
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Connection lost!");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Called when a message arrives on the subscribed topic
                    String payload = new String(message.getPayload());
                    Log.d("MQTT", "Message received on topic: " + topic + ", message: " + payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used in this example
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        String clientId = MqttClient.generateClientId();
        Log.d("ClientId", clientId);
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), MQTT_HOST, clientId);
        Log.d("Client", mqttAndroidClient.getClientId());

        try {
            token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Success!", "WOOOOOO!!!");
                    subscribe(mqttAndroidClient, sub_topic);
                    subscribe(mqttAndroidClient,"bmp");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Fail!", "WOOOOOO!!!");
                }
            });
        } catch (MqttException e) {
            Log.d("Fail!", "WOOOOOO!!!");
            e.printStackTrace();
        }
    }

    private void disconnect() {
        token.getActionCallback().onFailure(token, new MqttException(MqttException.REASON_CODE_CLIENT_DISCONNECTING));
        token = null;
    }
}