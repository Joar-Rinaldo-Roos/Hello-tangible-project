package com.example.mysensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

public class MqttController {
    final String MQTT_HOST = "tcp://broker.hivemq.com:1883";
    final String sub_topic = "arduino/out/mamn01/groupD/arduinoSensor";
    final String pub_topic = "arduino/in/mamn01/groupD/arduinoSensor";
    final String pub_message = "Hello World!";

    public String values = "";

    MqttAndroidClient mqttAndroidClient;

    private IMqttToken token;

    public MqttController(Context context) {
        Log.d("Bout to connect", "connecting");
        connect(context);
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
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect(Context context) {
        String clientId = MqttClient.generateClientId();
        Log.d("ClientId", clientId);
        mqttAndroidClient = new MqttAndroidClient(context, MQTT_HOST, clientId);
        Log.d("Client", mqttAndroidClient.getClientId());

        try {
            token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Success!", "WOOOOOO!!!");
                    subscribe(mqttAndroidClient, sub_topic);
                    mqttAndroidClient.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.d("file", message.toString());
                            // tt.setText(message.toString());
                            values = message.toString();
                        }
                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
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
