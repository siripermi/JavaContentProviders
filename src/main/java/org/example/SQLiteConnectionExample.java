package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.provider.BaseProvider;
import org.example.provider.db.BaseDao;
import org.example.provider.db.DatabaseHelper;

public class SQLiteConnectionExample extends BaseProvider {

    private static final String BROKER_URL = "tcp://localhost:1883"; // MQTT broker address
    private static final String CLIENT_ID = "JavaSample"; // Client ID
    private static final String TOPIC = "test/topic"; // Topic to subscribe to
    private static MqttClient mqttClient = null;

    public static void main(String[] args) {
        DatabaseHelper.getInstance();
        //connect mqtt
        connectToMqtt();
    }

    private static void connectToMqtt(){
        try {
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);


            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    System.out.println("Message received: " + payload);

                    String[] parts = payload.split(",");
                    if (parts.length == 3) {
                        String command = parts[0];
                        String key = parts[1];
                        String value = parts[2];
                        System.out.println("command " + command + " key: " + key + " value: " + value);

                        switch (command) {
                            case "insert":
                                insertData(key, value);
                                break;
                            case "update":
                                updateData(key, value);
                                break;
                            case "delete":
                                deleteData(key, value);
                                break;
                            case "specific":
                                getSpecificData(key, value);
                                break;
                            case "all":
                                getFullData();
                                break;
                            default:
                                System.out.println("Unknown command: " + command);
                        }
                    } else {
                        System.out.println("Invalid message format.");
                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            mqttClient.connect(connOpts);
            mqttClient.subscribe(TOPIC);

            System.out.println("Connected to broker: " + BROKER_URL);
            System.out.println("Subscribed to topic: " + TOPIC);

            System.out.println("Subscribed to topic: " + TOPIC);
           // while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("enter your operation to perform : ");

                String operation = scanner.nextLine();  // Read user input
                System.out.println("You have Selected Operation: " + operation);

                switch (operation) {
                    case "1":
                        String insertMessage = "insert,test,value";
                        publishMessage(insertMessage);
                        break;
                    case "2":
                        String updateMessage = "update,test,value_updated";
                        publishMessage(updateMessage);
                        break;
                    case "3":
                        String deleteMessage = "delete,test,nothing";
                        publishMessage(deleteMessage);
                        break;
                    case "4":
                        String printdata = "all,test,nothing";
                        publishMessage(printdata);
                        break;
                    case "5":
                        String data = "specific,primary_sw_version,nothing";
                        publishMessage(data);
                        break;
                    default:
                        publishMessage("all");
                }
           // }

            /*//print
            for(int i =0 ;i<100;i++) {
                String printdata = "all,test,nothing";
                publishMessage(printdata);
            }

            //update
            for(int i =0 ;i<100;i++) {
                String updateMessage = "update,test,value_"+i;
                publishMessage(updateMessage);
            }*/
            }catch(Exception e){
                e.printStackTrace();
            }

    }

    private static void publishMessage(String messageContent) throws MqttException {
        MqttMessage message = new MqttMessage(messageContent.getBytes());
        mqttClient.publish(TOPIC, message);
        System.out.println("Message published: " + messageContent);
    }

    private static void insertData(String key, String value) {
        String sqlInsert = "INSERT INTO profile (name,value) VALUES (?, ?)";
        Map<Integer, String> queryParams = new HashMap<>();
        queryParams.put(1, key);
        queryParams.put(2, value);
        BaseDao.queryDB(sqlInsert,queryParams,true);
    }
    private static void updateData(String key, String value) {
        String sqlUpdate = "UPDATE profile SET value = ? WHERE name = ?";
        Map<Integer, String> queryParams = new HashMap<>();
        queryParams.put(1, value);
        queryParams.put(2, key);

        String selection = "name = ?";
        String[] selectionArgs = new String[] {key,value};

        BaseDao.queryDB(sqlUpdate,queryParams,true);
    }

    private static void deleteData(String key, String value) {
        String sqlDelete = "DELETE FROM profile WHERE name = ?";
        Map<Integer, String> queryParams = new HashMap<>();
        queryParams.put(1, key);
        BaseDao.queryDB(sqlDelete,queryParams,true);
    }

    private static void getSpecificData(String key, String value){
        String sql = "select * from profile where name = ?";
        Map<Integer, String> queryParams = new HashMap<>();
        queryParams.put(1, key);
        getData(sql,queryParams);
    }

    private static void getFullData(){
        String sql = "select * from profile";
        getData(sql,null);
    }

    private static void getData(String sql, Map<Integer, String> queryParams) {

        ResultSet resultSet = BaseDao.queryDB(sql, queryParams,false);
        if (resultSet == null) {
            System.out.println("error");
            return;
        }
        try {
            while (resultSet.next()) {
                // read the result set
                System.out.println("name = " + resultSet.getString("name"));
                System.out.println("id = " + resultSet.getInt("value"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected String getTableName() {
        return "";
    }

    @Override
    protected long incrementTcc() {
        return 0;
    }

    @Override
    public long getTcc() {
        return 0;
    }
}
